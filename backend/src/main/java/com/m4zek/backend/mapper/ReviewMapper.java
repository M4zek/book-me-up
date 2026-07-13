package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.dto.read.ReviewStatisticsResponse;
import com.m4zek.backend.model.dto.read.UserReviewResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReviewMapper {

    private final UserMapper userMapper;
    private final CompanyOfferMapper offerMapper;

    private ReviewMapper(UserMapper userMapper, CompanyOfferMapper offerMapper) {
        this.userMapper = userMapper;
        this.offerMapper = offerMapper;
    }

    public ReviewStatisticsResponse reviewsToReviewsResponse(List<Object[]> reviews) {
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

    public UserReviewResponse reviewsToUserReviewResponse(Review review) {
        return UserReviewResponse.builder()
                .id(review.getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .created_at(toOffset(review.getCreatedDate()))
                .updated_at(toOffset(review.getModifiedDate()))
                .author(this.userMapper.toUserResponse(review.getUser()))
                .offer(this.offerMapper.companyOfferToCompanyOfferResponse(review.getCompanyOffer()))
                .build();
    }



    private static OffsetDateTime toOffset(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("Europe/Warsaw")).toOffsetDateTime();
    }
}
