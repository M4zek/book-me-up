package com.m4zek.backend.model.dto.read;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String birthdate;
    private String phoneNumber;
    private byte[] avatar;
}
