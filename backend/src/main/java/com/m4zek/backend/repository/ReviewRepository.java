package com.m4zek.backend.repository;

import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.projection.ReviewStatisticsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    boolean existsByUserIdAndCompanyOfferId(int userId, long companyOfferId);

    Review save(Review newReview);

    Optional<Review> findById(Integer id);

    @Query("""
        SELECT DISTINCT new com.m4zek.backend.model.projection.ReviewStatisticsProjection(
                COUNT(r),
                AVG (r.rating))
        FROM reviews r
        JOIN r.companyOffer o
        WHERE o.company.id = :companyId
          AND r.rating IS NOT NULL
        """)
    ReviewStatisticsProjection findReviewStatsByCompanyId(@Param("companyId") int companyId);

    @Query("""
        SELECT r.rating, COUNT(r)
        FROM reviews r
        WHERE r.companyOffer.company.id = :companyId
        GROUP BY r.rating
    """)
    List<Object[]> findAllByCompanyId(@Param("companyId") int companyId);


    @Query("""
    SELECT r FROM reviews r
    JOIN r.companyOffer o
    WHERE o.company.id = :companyId
    AND (:rating IS NULL OR r.rating = :rating)
""")
    Page<Review> findAllByCompanyIdAndRating(@Param("companyId") int companyId, Pageable pageable, Integer rating);
}
