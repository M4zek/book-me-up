package com.m4zek.backend.model.dto.write;

import com.m4zek.backend.model.RoomType;
import lombok.Data;

import java.util.List;

@Data
public class RoomRequest {
    private RoomType type;
    private String name;
    private int ownerId;
    private List<Integer> memberIds;
}
