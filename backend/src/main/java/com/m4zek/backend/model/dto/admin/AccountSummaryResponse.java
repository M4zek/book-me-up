package com.m4zek.backend.model.dto.admin;

import java.time.LocalDateTime;

public record AccountSummaryResponse(
        Integer id,
        String avatarUrl,
        String name,
        LocalDateTime createdAt
){}