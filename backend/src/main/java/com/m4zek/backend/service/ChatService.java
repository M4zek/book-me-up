package com.m4zek.backend.service;


import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.exception.RoomNotFoundException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.ChatMapper;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.ChatMessageResponse;
import com.m4zek.backend.model.dto.read.RoomResponse;
import com.m4zek.backend.model.dto.write.ChatMessageRequest;
import com.m4zek.backend.model.dto.write.RoomRequest;
import com.m4zek.backend.repository.ChatMessageRepository;
import com.m4zek.backend.repository.RoomRepository;
import com.m4zek.backend.repository.RoomUserRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
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
                .map(ChatMapper::roomToRoomResponse)
                .toList();
        return new PageImpl<>(rooms_response, pageable, rooms.getTotalElements());
    }

    public RoomResponse createConversationRoom(RoomRequest roomRequest) {

        System.out.println(roomRequest.toString());

        // Find the owner user
        User owner = this.userRepository.findById(roomRequest.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("Owner id not found"));

        // Find the all members. If one of them not found throw exception
        Set<User> members = roomRequest.getMemberIds().stream()
                .map(id -> this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Member not found")))
                .collect(Collectors.toSet());

        Room room = new Room();

        // Created roles in chat
        List<RoomUser> roomUsers = new ArrayList<>();
        roomUsers.add(new RoomUser(room, owner, RoomRole.OWNER));
        members.forEach(member -> {roomUsers.add(new RoomUser(room, member, RoomRole.MEMBER));});

        room.getRoles().addAll(roomUsers);

        if(roomRequest.isGroup() && members.size() > 1) {
            room.setType(RoomType.GROUP);
            room.setName(roomRequest.getName());
        } else {
            room.setType(RoomType.PRIVATE);
        }

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
