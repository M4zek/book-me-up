package com.m4zek.backend.model.dto.read;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {
    private int id;
    private String comment;
    private Integer rating;
    private String author_name;
}
