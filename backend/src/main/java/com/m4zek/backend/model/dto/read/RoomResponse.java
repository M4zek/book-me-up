package com.m4zek.backend.model.dto.read;

import com.m4zek.backend.model.RoomType;
import com.m4zek.backend.model.projection.MemberProjection;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Builder
@Data
public class RoomResponse {

    private int id;
    private String name;
    private List<MemberProjection> memberProjections;
    private RoomType roomType;
    private ChatMessageResponse lastMessage;


}
