package com.m4zek.backend.model.dto.write;

import lombok.Data;

import java.util.List;

@Data
public class RoomRequest {
    private boolean group;
    private String name;
    private int ownerId;
    private List<Integer> memberIds;
}
