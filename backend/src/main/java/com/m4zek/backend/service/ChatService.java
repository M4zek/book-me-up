package com.m4zek.backend.service;


import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.exception.RoomNotFoundException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.ChatMapper;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.ChatMessageResponse;
import com.m4zek.backend.model.dto.read.MessageReadReceipt;
import com.m4zek.backend.model.dto.read.RoomResponse;
import com.m4zek.backend.model.dto.write.ChatMessageRequest;
import com.m4zek.backend.model.dto.write.RoomRequest;
import com.m4zek.backend.repository.ChatMessageRepository;
import com.m4zek.backend.repository.RoomRepository;
import com.m4zek.backend.repository.RoomUserRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import jakarta.persistence.EntityExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {


    private final ChatMessageRepository chatRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomUserRepository roomUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(
            ChatMessageRepository chatRepository,
            RoomRepository roomRepository,
            UserRepository userRepository,
            RoomUserRepository roomUserRepository,
            SimpMessagingTemplate simpMessagingTemplate) {
        this.chatRepository = chatRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomUserRepository = roomUserRepository;
        this.messagingTemplate = simpMessagingTemplate;
    }



    public Page<RoomResponse> getUserRooms(int user_id, Pageable pageable) {
        int logged_user_id = this.getLoggedUserId();
        if (logged_user_id != user_id) {
            throw new AccessDeniedException("Access denied");
        }

        Page<Room> rooms = this.roomRepository.findAllByUserIdAndSortByLastMessage(user_id, pageable);

        List<RoomResponse> rooms_response = rooms.stream()
                .map(room -> {
                    Optional<RoomUser> userRoomOpt = room.getRoles().stream()
                            .filter(ur -> ur.getUser().getId() == logged_user_id)
                            .findFirst();

                    long lastReadId = userRoomOpt
                            .map(RoomUser::getLastReadMessage)
                            .map(Message::getId)
                            .orElse(0);

                    long unreadMsg = room.getMessages().stream()
                            .filter(m -> m.getId() > lastReadId)
                            .count();

                    return ChatMapper.roomToRoomResponse(room, unreadMsg);
                })
                .toList();

        return new PageImpl<>(rooms_response, pageable, rooms.getTotalElements());
    }

    public RoomResponse createConversationRoom(RoomRequest roomRequest) {


        // Find the owner user
        User owner = this.userRepository.findById(roomRequest.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("Owner id not found"));

        // Find the all members. If one of them not found throw exception
        Set<User> members = roomRequest.getMemberIds().stream()
                .map(id -> this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Member not found")))
                .collect(Collectors.toSet());

        // Check if private room between two users already exists throw exception
        if(roomRequest.getType().equals(RoomType.PRIVATE)){
            int memberId = roomRequest.getMemberIds().size() == 1 ? roomRequest.getMemberIds().getFirst() : 0;
            this.roomUserRepository.findPrivateConversation(roomRequest.getOwnerId(), memberId).ifPresent(r -> {
                        throw new EntityExistsException("Room already exists");
                    });
        }

        Room room = new Room();

        // Created roles in chat
        List<RoomUser> roomUsers = new ArrayList<>();
        roomUsers.add(new RoomUser(room, owner, RoomRole.OWNER));
        members.forEach(member -> roomUsers.add(new RoomUser(room, member, RoomRole.MEMBER)));

        room.getRoles().addAll(roomUsers);

        room.setType(roomRequest.getType());
        room.setName(roomRequest.getType().equals(RoomType.GROUP) ? roomRequest.getName() : null);

        Room savedRoom = this.roomRepository.save(room);

        return ChatMapper.roomToRoomResponse(savedRoom);
    }


    public void sendMessage(ChatMessageRequest message, Principal principal) {
        Room room = roomRepository.findById(message.getRoom_id())
                .orElseThrow(RoomNotFoundException::new);

        User sender = userRepository.findByAddressEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));


        // Create row in table - message
        Message msg = chatRepository.save(
                new Message(message.getType(), message.getContent(), room, sender)
        );

        // Convert message to
        ChatMessageResponse msgToSend = ChatMapper.toChatMessageResponse(msg);

        // Send the message to all user which subscribe room
        String destination = "/topic/room/" + message.getRoom_id();
        messagingTemplate.convertAndSend(destination, msgToSend);

        // Send notification to all room members but not to message sender
        // Send to users who are not connected to the room
        room.getRoles().stream()
                .filter(r -> !r.getUser().equals(sender))
                .forEach(u -> {

                    long unreadMessageCount = u.getRoom().getMessages().stream()
                                        .filter(m -> m.getId() > u.getLastReadMessage().getId())
                                    .count();

                    RoomResponse roomResponse = ChatMapper.roomToRoomResponse(room, unreadMessageCount);

                    messagingTemplate.convertAndSendToUser(
                            u.getUser().getAddressEmail(),
                            "/queue/notification/chat",
                            roomResponse
                    );
                });
    }


    public void markLastMessageAsRead(MessageReadReceipt receipt, Principal principal) {
        User user = this.userRepository.findByAddressEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));

        RoomUser roomUser = this.roomUserRepository.findByRoomIdAndUserId(receipt.getRoom_id(), user.getId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        Message msg = chatRepository.findByIdAndRoomId(receipt.getMessage_id(), receipt.getRoom_id())
                .orElseThrow(() -> new RoomNotFoundException("Message not found"));

        roomUser.updateReadLastMessage(msg);
        roomUserRepository.save(roomUser);

        MessageReadReceipt payload = MessageReadReceipt.builder()
                .room_id(roomUser.getRoom().getId())
                .message_id(roomUser.getLastReadMessage().getId())
                .reader_id(roomUser.getUser().getId())
                .type(MessageType.RECEIPT)
                .build();

        // Send all subscriber that the user read the msg
        messagingTemplate.convertAndSend("/topic/room/" + receipt.getRoom_id(), payload);
    }


    public Page<ChatMessageResponse> getMessages(int room_id, Pageable pageable) {
        this.hasAccessToChat(room_id);
        Page<Message> messages = this.chatRepository.findAllByRoomId(room_id, pageable);
        List<ChatMessageResponse> response = messages.stream()
                .map(ChatMapper::toChatMessageResponse)
                .toList();
        return new PageImpl<>(response, pageable, messages.getTotalElements());
    }


    public void hasAccessToChat(int room_id) throws AccessDeniedException {
        int logged_user_id = this.getLoggedUserId();

        if(!this.roomUserRepository.existsByRoomIdAndUserId(room_id, logged_user_id)) {
            throw new AccessDeniedException("Access denied - You are not authorized to view this resource");
        }
    }

    private int getLoggedUserId() {
        MyUserDetails user = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
}
