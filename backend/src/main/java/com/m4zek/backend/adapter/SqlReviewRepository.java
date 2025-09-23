package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Review;
import com.m4zek.backend.repository.ReviewRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlReviewRepository extends ReviewRepository, JpaRepository<Review, Integer> {
}
