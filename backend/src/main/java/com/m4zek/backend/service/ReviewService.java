package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.ReviewBadRequestException;
import com.m4zek.backend.exception.ReviewExistsException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.ReviewMapper;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.UserReviewResponse;
import com.m4zek.backend.model.dto.write.ReviewRequest;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.ReviewRepository;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CompanyOfferRepository companyOfferRepository;


    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, CompanyOfferRepository companyOfferRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.companyOfferRepository = companyOfferRepository;
    }


    public UserReviewResponse createNewReview(ReviewRequest reviewRequest) {

        if(reviewRepository.existsByUserIdAndCompanyOfferId(reviewRequest.getAuthor_id(), reviewRequest.getCompany_offer_id())){
            throw new ReviewExistsException("You can only add one review to an offer!");
        }

        User author = userRepository.findById(reviewRequest.getAuthor_id())
                .orElseThrow(() -> new UserNotFoundException("Author not found"));

        CompanyOffer companyOffer = companyOfferRepository.findById(reviewRequest.getCompany_offer_id())
                .orElseThrow(() -> new CompanyNotFoundException("Company offer not found"));

        if(companyOffer.getReservations().stream()
                .noneMatch(reservation -> reservation.getUser().equals(author))){
            throw new ReviewBadRequestException("Cannot add review without reservation!");
        }

        Review savedReview = this.reviewRepository.save(new Review(
                reviewRequest.getComment(),
                reviewRequest.getRating(),
                companyOffer,
                author
        ));

        return ReviewMapper.reviewsToUserReviewResponse(savedReview);
    }



    public Page<UserReviewResponse> getCompanyReviews(Pageable pageable, int company_id, Integer rating) {
        Page<Review> companyReviews = this.reviewRepository.findAllByCompanyIdAndRating(company_id, pageable, rating);
        List<UserReviewResponse> userReviewResponses = companyReviews.stream()
                .map(ReviewMapper::reviewsToUserReviewResponse)
                .toList();
        return new PageImpl<>(userReviewResponses, pageable, companyReviews.getTotalElements());
    }


}
