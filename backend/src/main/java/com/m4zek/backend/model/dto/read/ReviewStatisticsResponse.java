package com.m4zek.backend.model.dto.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReviewStatisticsResponse {
    private double rating;
    private int totalReviews;
    private Map<Integer, Integer> ratingCounts;
}
