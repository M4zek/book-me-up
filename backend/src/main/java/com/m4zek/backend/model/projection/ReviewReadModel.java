package com.m4zek.backend.model.projection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewReadModel {
    private int id;
    private String comment;
    private Integer rating;
    private String author_name;
}
