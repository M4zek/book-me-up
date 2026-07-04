package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.UserReviewResponse;
import com.m4zek.backend.model.dto.write.ReviewPatchRequest;
import com.m4zek.backend.model.dto.write.ReviewRequest;
import com.m4zek.backend.service.facade.ReviewFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class CompanyReviewController {


    private final ReviewFacade reviewFacade;

    public CompanyReviewController(ReviewFacade reviewFacade) {
        this.reviewFacade = reviewFacade;
    }

    /*
            PRIVATE ENDPOINT
     */
    @PostMapping("/v1/companies/reviews")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserReviewResponse> createNewReview(@RequestBody @Valid ReviewRequest reviewRequest) {
        UserReviewResponse savedReview = this.reviewFacade.createReview(reviewRequest);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    // Endpoint for updating the review by their owner
    @PatchMapping("/v1/companies/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserReviewResponse> updateReview(
            @Positive(message = "Review id must be positive number")
            @NotNull(message = "Review id must not be null")
            @PathVariable Integer reviewId,
            @RequestBody @Valid ReviewPatchRequest updateReview) {
         UserReviewResponse response = this.reviewFacade.updateReview(reviewId, updateReview);
         return ResponseEntity.ok(response);
    }


    /*
        PUBLIC ENDPOINT
     */
    @GetMapping("/public/companies/{companyId}/reviews")
    public ResponseEntity<Page<UserReviewResponse>> getCompanyReviews(
            Pageable pageable,
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestParam(required = false)
            @Range(min = 1, max = 5, message = "Rating must be from 1 to 5") Integer rating) {
        Page<UserReviewResponse> response = this.reviewFacade.readCompanyReviews(companyId, rating, pageable);
        return ResponseEntity.ok(response);
    }

}
