package com.m4zek.backend.model.dto.admin;

public record CompanyRankingResponse(
    Integer id,
    String avatarUrl,
    String companyName,
    Long reservationCount
){}
