package com.m4zek.backend.model.dto.read;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserReviewResponse {
    private int id;
    private String comment;
    private Integer rating;
    private OffsetDateTime created_at;
    private OffsetDateTime updated_at;
    private UserResponse author;
    private CompanyOfferResponse offer;
}
