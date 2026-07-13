package com.m4zek.backend.mapper;


import com.m4zek.backend.model.Message;
import com.m4zek.backend.model.MessageType;
import com.m4zek.backend.model.Room;
import com.m4zek.backend.model.RoomUser;
import com.m4zek.backend.model.dto.read.ChatMessageResponse;
import com.m4zek.backend.model.dto.read.MessageReadReceipt;
import com.m4zek.backend.model.dto.read.RoomResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class RoomMessageMapper {

    private final UserMapper userMapper;

    private RoomMessageMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public ChatMessageResponse toChatMessageResponse(Message message) {
        return ChatMessageResponse
                .builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getMessageType())
                .sender(this.userMapper.userToMemberProjection(message.getUser()))
                .createdDate(toOffset(message.getCreatedDate()))
                .readBy(
                        message.getRoom().getRoles().stream()
                                .filter(roles ->
                                        roles.getLastReadMessage() != null && roles.getLastReadMessage().getId() == message.getId()
                                )
                                .map(role -> this.userMapper.userToMemberProjection(role.getUser())).toList()
                )
                .build();
    }

    public RoomResponse roomToRoomResponse(Room room, long numOfUnreadMessage) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .roomType(room.getType())
                .memberProjections(
                        room.getRoles().stream()
                                .map(role ->
                                        this.userMapper.userToMemberProjection(role.getUser())
                                ).toList()
                )
                .lastMessage(
                        room.getMessages().isEmpty() ?
                                null : toChatMessageResponse(room.getMessages().getLast())
                )
                .numOfUnreadMessages(numOfUnreadMessage)
                .build();
    }

    public MessageReadReceipt toMessageReadReceipt(RoomUser roomUser){
        return MessageReadReceipt.builder()
                .room_id(roomUser.getRoom().getId())
                .message_id(roomUser.getLastReadMessage().getId())
                .reader_id(roomUser.getUser().getId())
                .type(MessageType.RECEIPT)
                .build();
    }

    private static ZonedDateTime toOffset(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("Europe/Warsaw")).toOffsetDateTime().toZonedDateTime();
    }
}
