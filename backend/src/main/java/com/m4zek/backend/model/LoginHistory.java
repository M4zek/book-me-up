package com.m4zek.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "login_history")
public class LoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer userId;

    private String email;

    private String ipAddress;

    private String userAgent;

    private boolean success;

    private String failureReason;

    public LoginHistory() {}

    public LoginHistory(Integer userId, String email, String ipAddress, String userAgent, boolean success, String failureReason) {
        this.userId = userId;
        this.email = email;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.success = success;
        this.failureReason = failureReason;
    }
}
