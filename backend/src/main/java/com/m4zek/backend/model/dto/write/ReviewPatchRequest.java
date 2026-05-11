package com.m4zek.backend.model.dto.write;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPatchRequest {
    private String comment;
    private Integer rating;
}
