package com.m4zek.backend.service;

import com.m4zek.backend.mapper.RoomMessageMapper;
import com.m4zek.backend.model.Room;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.ChatMessageResponse;
import com.m4zek.backend.model.dto.read.MessageReadReceipt;
import com.m4zek.backend.model.dto.read.RoomResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomMessageMapper mapper;

    public MessageSenderService(SimpMessagingTemplate messagingTemplate, RoomMessageMapper mapper) {
        this.messagingTemplate = messagingTemplate;
        this.mapper = mapper;
    }

    public void sendMessageToRoom(Integer roomId, ChatMessageResponse message){
        String destination = "/topic/room/" + roomId;
        this.messagingTemplate.convertAndSend(destination, message);
    }

    public void sendMessageToRoom(Integer roomId, MessageReadReceipt message){
        String destination = "/topic/room/" + roomId;
        this.messagingTemplate.convertAndSend(destination, message);
    }

    public void sendRoomNotification(Room room, User sender){
        room.getRoles().stream()
                .filter(r -> !r.getUser().equals(sender))
                .forEach(r -> {
                    long undearMessageCount = r.getLastReadMessage() != null ?
                            r.getRoom().getMessages().stream()
                                    .filter(m -> m.getId() > r.getLastReadMessage().getId())
                                    .count() : r.getRoom().getMessages().size();

                    RoomResponse mappedResponse = this.mapper.roomToRoomResponse(room, undearMessageCount);

                    this.messagingTemplate.convertAndSendToUser(
                            r.getUser().getAddressEmail(),
                            "/queue/notification/chat",
                            mappedResponse
                    );
                });
    }


}
