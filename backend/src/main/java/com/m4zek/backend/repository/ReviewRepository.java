package com.m4zek.backend.repository;

import com.m4zek.backend.model.Review;

import java.util.List;

public interface ReviewRepository {

    boolean existsByUserIdAndCompanyOfferId(int userId, long companyOfferId);

    Review save(Review newReview);

    List<Review> readAllByCompanyOfferId(long companyOfferId);
}
