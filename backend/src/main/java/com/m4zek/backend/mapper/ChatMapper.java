package com.m4zek.backend.mapper;


import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.ChatMessageResponse;
import com.m4zek.backend.model.dto.read.RoomResponse;
import com.m4zek.backend.model.projection.MemberProjection;

import java.time.ZoneId;

public class ChatMapper {

    private ChatMapper() {}

    public static ChatMessageResponse toChatMessageResponse(Message message) {
        return ChatMessageResponse
                .builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getMessageType())
                .sender(
                        MemberProjection.builder()
                                .id(message.getUser().getId())
                                .firstName(message.getUser().getUserData().getFirstName())
                                .lastName(message.getUser().getUserData().getLastName())
                                .avatar(ImageMapper.byteImageToBase64(message.getUser().getUserData().getPhoto()))
                                .role(message.getRoom().getRoles().stream().filter(
                                        role -> role.getUser().getId() == message.getUser().getId())
                                        .findFirst()
                                        .map(RoomUser::getRole).orElse(RoomRole.MEMBER)
                                )
                                .build()
                )
                .createdDate(message.getCreatedDate().atZone(ZoneId.of("Europe/Warsaw")).toOffsetDateTime().toZonedDateTime())
                .readBy(message.getRoom().getRoles().stream()
                                .filter(roles ->
                                    roles.getLastReadMessage() != null && roles.getLastReadMessage().getId() == message.getId()
                                )
                                .map(role -> {
                                    User user = role.getUser();
                                    return MemberProjection.builder()
                                            .id(user.getId())
                                            .firstName(user.getUserData().getFirstName())
                                            .lastName(user.getUserData().getLastName())
                                            .avatar(ImageMapper.byteImageToBase64(user.getUserData().getPhoto()))
                                            .build();
                                })
                                .toList()
                )
                .build();
    }


    public static RoomResponse roomToRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .roomType(room.getType())
                .memberProjections(room.getRoles().stream()
                        .map(role -> {
                            User member = role.getUser();
                            return MemberProjection.builder()
                                    .id(member.getId())
                                    .firstName(member.getUserData().getFirstName())
                                    .lastName(member.getUserData().getLastName())
                                    .avatar(ImageMapper.byteImageToBase64(member.getUserData().getPhoto()))
                                    .role(role.getRole()).build();
                        }).toList())
                .lastMessage(room.getMessages().isEmpty() ?
                        null : toChatMessageResponse(room.getMessages().getLast()))
                .numOfUnreadMessages(0)
                .build();
    }

    public static RoomResponse roomToRoomResponse(Room room, long numOfUnreadMessages) {
        RoomResponse response = roomToRoomResponse(room);
        response.setNumOfUnreadMessages(numOfUnreadMessages);
        return response;
    }

}
