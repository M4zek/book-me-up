package com.m4zek.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "refresh_token")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String refreshToken;

    private Instant expireDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public RefreshToken() {}

    public RefreshToken(String refresh_token, Instant expireDate, User user) {
        this.refreshToken = refresh_token;
        this.expireDate = expireDate;
        this.user = user;
    }

    public void updateRefreshToken(String refresh_token, Instant expireDate) {
        this.refreshToken = refresh_token;
        this.expireDate = expireDate;
    }

}
