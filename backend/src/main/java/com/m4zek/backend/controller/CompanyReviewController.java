package com.m4zek.backend.controller;

import com.m4zek.backend.model.projection.ReviewReadModel;
import com.m4zek.backend.model.projection.ReviewWriteModel;
import com.m4zek.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies/reviews")
public class CompanyReviewController {

    private final ReviewService reviewService;

    public CompanyReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewReadModel> createNewReview(@RequestBody @Valid ReviewWriteModel reviewWriteModel) {
        ReviewReadModel savedReview = this.reviewService.createNewReview(reviewWriteModel);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }


    @GetMapping("/{company_offer_id}")
    public ResponseEntity<List<ReviewReadModel>> readAllCompanyOfferReviews(@PathVariable long company_offer_id){
        return ResponseEntity.ok(this.reviewService.readAllCompanyOfferReviews(company_offer_id));
    }


}
