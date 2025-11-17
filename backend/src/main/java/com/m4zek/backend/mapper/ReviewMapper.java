package com.m4zek.backend.mapper;

import com.m4zek.backend.model.dto.read.ReviewStatisticsResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewMapper {

    private ReviewMapper() {}

    public static ReviewStatisticsResponse reviewsToReviewsResponse(List<Object[]> reviews) {
        Map<Integer, Integer> ratingCounts = new HashMap<>();
        int totalReviews = 0;
        double weightedSum = 0.0;

        for (Object[] row : reviews) {
            Integer rating = ((Number) row[0]).intValue();
            Integer count = ((Number) row[1]).intValue();

            ratingCounts.put(rating, count);
            totalReviews += count;
            weightedSum += rating * count;
        }

        double averageRating = totalReviews > 0 ? weightedSum / totalReviews : 0.0;

        return ReviewStatisticsResponse.builder()
                .rating(averageRating)
                .totalReviews(totalReviews)
                .ratingCounts(ratingCounts)
                .build();
    }

}
