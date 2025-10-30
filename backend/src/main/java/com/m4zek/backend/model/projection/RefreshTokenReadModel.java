package com.m4zek.backend.model.projection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenReadModel {
    private String accessToken;
    private String refreshToken;

    public RefreshTokenReadModel(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}