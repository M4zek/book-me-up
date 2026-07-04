package com.m4zek.backend.service.facade;


import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.mapper.RoomMessageMapper;
import com.m4zek.backend.model.Message;
import com.m4zek.backend.model.Room;
import com.m4zek.backend.model.RoomUser;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.ChatMessageResponse;
import com.m4zek.backend.model.dto.read.MessageReadReceipt;
import com.m4zek.backend.model.dto.read.RoomResponse;
import com.m4zek.backend.model.dto.write.ChatMessageRequest;
import com.m4zek.backend.model.dto.write.RoomRequest;
import com.m4zek.backend.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatFacade {

    private final UserService userService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final RoomUserService roomUserService;
    private final MessageSenderService senderService;

    private final RoomMessageMapper roomMessageMapper;

    public ChatFacade(UserService userService, MessageService messageService, RoomService roomService, RoomUserService roomUserService, MessageSenderService senderService, RoomMessageMapper roomMessageMapper) {
        this.userService = userService;
        this.messageService = messageService;
        this.roomService = roomService;
        this.roomUserService = roomUserService;
        this.senderService = senderService;
        this.roomMessageMapper = roomMessageMapper;
    }


    public RoomResponse createRoom(RoomRequest request){

        // Check if private room exist with the same two persons throw exception
        this.roomUserService.checkPrivateRoomExists(request);

        // Find room owner
        User owner = this.userService.findUserById(request.getOwnerId());

        // Find all room members
        Set<User> members = request.getMemberIds().stream()
                .map(this.userService::findUserById)
                .collect(Collectors.toSet());

        // Save room
        Room room = this.roomService.createAndSaveRoom(owner, members, request);

        // Return mapped room
        return this.roomMessageMapper.roomToRoomResponse(room, 0);
    }


    @Transactional
    public void sendMessage(ChatMessageRequest message, Principal principal){
        Room room = this.roomService.findByIdOrThrow(message.getRoom_id());

        User sender = this.userService.findUserByEmail(principal.getName());

        Message msg = this.messageService.createAndSaveMessage(message, room, sender);

        ChatMessageResponse msgToSend = this.roomMessageMapper.toChatMessageResponse(msg);

        this.senderService.sendMessageToRoom(room.getId(), msgToSend);

        this.senderService.sendRoomNotification(room, sender);

//        Map<User, RoomResponse> userRoomResponseMap = room.getRoles()
//                .stream()
//                .filter(r -> !r.getUser().equals(sender))
//                .collect(
//                        Collectors.toMap(
//                                RoomUser::getUser,
//                                r -> {
//                                    long unreadMessageCount = r.getLastReadMessage() != null ?
//                                            r.getRoom().getMessages().stream()
//                                                    .filter(m -> m.getId() > r.getLastReadMessage().getId())
//                                                    .count() : r.getRoom().getMessages().size();
//
//                                    return this.roomMessageMapper.roomToRoomResponse(room, unreadMessageCount);
//                                }
//
//                        )
//                );

//        this.senderService.sendRoomNotification(userRoomResponseMap);
    }

    // Method to mark last read message by user in room
    public void markLastMessageAsRead(MessageReadReceipt receipt, Principal principal){
        // Find the user who read the message
        User user = this.userService.findUserByEmail(principal.getName());

        // Find the relation between user and room
        RoomUser roomUser = this.roomUserService.findByRoomIdAndUserId(receipt.getRoom_id(), user.getId());

        // Find lastMessage who was read by user
        Message lastMessage = this.messageService.findLastMessageInRoom(receipt.getRoom_id());

        // Update the last message who user read
        roomUser = this.roomUserService.updateLastMessageReadByUser(lastMessage, roomUser);

        // Convert message into receipt who message was last read
        MessageReadReceipt readReceipt = this.roomMessageMapper.toMessageReadReceipt(roomUser);

        // Send receipt into all users in room
        this.senderService.sendMessageToRoom(roomUser.getRoom().getId(), readReceipt);
    }


    // Read messages from room
    public Page<ChatMessageResponse> readMessagesFromRoom(int roomId, Pageable pageable){
        // Get logged user from security
        User loggedUser = this.userService.findLoggedUser();

        // Check the user has access to the chat room if not throw exception
        this.roomUserService.hasAccessToRoom(roomId, loggedUser);

        // Find all messages in room with given id
        Page<Message> messages = this.messageService.findMessagesInRoom(roomId, pageable);

        // Mapped messages to response
        List<ChatMessageResponse> mappedMessages = messages.stream()
                .map(this.roomMessageMapper::toChatMessageResponse)
                .toList();

        // Create pagination response
        return new PageImpl<>(mappedMessages, pageable, messages.getTotalElements());
    }


    // Read all user chat rooms
    public Page<RoomResponse> readUserRoom(int userId, Pageable pageable){
        User loggedUser = this.userService.findLoggedUser();

        // If logged user try download rooms belongs to another user throw exception
        if(loggedUser.getId() != userId) throw new AccessDeniedException("Access denied");

        // Read all user rooms with sorted by last message
        Page<Room> roms = this.roomService.findUserRooms(userId, pageable);

        // Mapping to response
        List<RoomResponse> roomResponse =
                roms.stream()
                        .map(room -> {
                            Optional<RoomUser> userRoomOpt = room.getRoles().stream()
                                    .filter(ur -> ur.getUser().getId() == loggedUser.getId())
                                    .findFirst();

                            long lastReadId = userRoomOpt
                                    .map(RoomUser::getLastReadMessage)
                                    .map(Message::getId)
                                    .orElse(0);

                            long unreadMsg = room.getMessages().stream()
                                    .filter(m -> m.getId() > lastReadId)
                                    .count();

                            return this.roomMessageMapper.roomToRoomResponse(room, unreadMsg);
                        }).toList();

        return new PageImpl<>(roomResponse, pageable, roms.getTotalElements());
    }


}
