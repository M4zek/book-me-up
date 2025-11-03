package com.m4zek.backend.model.dto.read;

import lombok.Data;

import java.util.List;

@Data
public class AuthResponse {

    private int id;
    private String token;
    private String refreshToken;
    private List<String> roles;

    public AuthResponse(int id, String token, String refreshToken, List<String> roles) {
        this.id = id;
        this.token = token;
        this.refreshToken = refreshToken;
        this.roles = roles;
    }
}
