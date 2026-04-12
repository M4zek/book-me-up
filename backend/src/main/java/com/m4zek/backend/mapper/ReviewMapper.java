package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.dto.read.ReviewStatisticsResponse;
import com.m4zek.backend.model.dto.read.UserReviewResponse;

import java.time.ZoneId;
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

    public static UserReviewResponse reviewsToUserReviewResponse(Review review) {
        return UserReviewResponse.builder()
                .id(review.getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .created_at(review.getCreatedDate().atZone(ZoneId.of("Europe/Warsaw")).toOffsetDateTime())
                .updated_at(review.getModifiedDate().atZone(ZoneId.of("Europe/Warsaw")).toOffsetDateTime())
                .author(UserMapper.toUserResponse(review.getUser()))
                .offer(CompanyOfferMapper.companyOfferToCompanyOfferResponse(review.getCompanyOffer()))
                .build();
    }



}
