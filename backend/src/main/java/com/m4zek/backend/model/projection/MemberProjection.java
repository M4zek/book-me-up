package com.m4zek.backend.model.projection;

import com.m4zek.backend.model.RoomRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberProjection {
    private int id;
    private String firstName;
    private String lastName;
    private String avatar;
    private RoomRole role;
}
