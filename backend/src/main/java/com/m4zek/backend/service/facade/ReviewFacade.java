package com.m4zek.backend.service.facade;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.mapper.ReviewMapper;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.UserReviewResponse;
import com.m4zek.backend.model.dto.write.ReviewPatchRequest;
import com.m4zek.backend.model.dto.write.ReviewRequest;
import com.m4zek.backend.service.CompanyOfferService;
import com.m4zek.backend.service.ReviewService;
import com.m4zek.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewFacade {

    private final ReviewService reviewService;
    private final CompanyOfferService companyOfferService;
    private final UserService userService;

    private final ReviewMapper reviewMapper;

    public ReviewFacade(ReviewService reviewService, CompanyOfferService companyOfferService, UserService userService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.companyOfferService = companyOfferService;
        this.userService = userService;
        this.reviewMapper = reviewMapper;
    }

    // Method to create new review
    public UserReviewResponse createReview(ReviewRequest request){
        // If user already have review to offer throw exception
        this.reviewService.isUserAlreadyHaveReview(request.getAuthor_id(), request.getCompany_offer_id());

        // Find author
        User author = this.userService.findUserById(request.getAuthor_id());

        // Find offer to review
        CompanyOffer offer = companyOfferService.findCompanyOffer(request.getCompany_offer_id())
                .orElseThrow(() -> new CompanyNotFoundException("Company offer not found"));

        // Create and save review
        Review savedReview = this.reviewService.createAndSaveReview(author, offer, request);

        // Return mapped review
        return this.reviewMapper.reviewsToUserReviewResponse(savedReview);
    }

    // Find company reviews (Filtering with rating. PAGINATION)
    public Page<UserReviewResponse> readCompanyReviews(int companyId, Integer rating, Pageable pageable){
        // Find Company reviews (Can Be filtering by rating)
        Page<Review> reviews = this.reviewService.findCompanyReviews(pageable, companyId, rating);

        // Mapped reviews to UserReviewResponse list
        List<UserReviewResponse> response = reviews.stream()
                .map(this.reviewMapper::reviewsToUserReviewResponse)
                .toList();

        // Return pagination mapped reviews
        return new PageImpl<>(response, pageable, reviews.getTotalElements());
    }



    // Method for updating review rating or comment
    // Only owner has access to do this
    public UserReviewResponse updateReview(int reviewId, ReviewPatchRequest request){
        // Find user calling this method
        User loggedUser = this.userService.findLoggedUser();

        // Find review or throw an exception
        Review review = this.reviewService.findReviewByIdOrElseThrow(reviewId);

        // Update review (Comment, rating)
        review = this.reviewService.updateReview(review, loggedUser, request);

        // Return mapped review
        return this.reviewMapper.reviewsToUserReviewResponse(review);
    }


}
