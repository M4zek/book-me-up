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
import com.m4zek.backend.model.dto.write.ReviewPatchRequest;
import com.m4zek.backend.model.dto.write.ReviewRequest;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.ReviewRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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

    /**
     * Method for updating review by id and data including in DTO
     * @param reviewId - id for updating review
     * @param patchData - DTO, data for update review (new comment , new rating)
     * @return Updated review or exception not found review by id
     */
    public UserReviewResponse updateReview(Integer reviewId, ReviewPatchRequest patchData) {
        Review reviewToUpdate = this.reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CompanyNotFoundException("Company review not found"));


        // If the person calling the endpoint is not the owner of the review
        // , an “Access Denied” exception must be thrown.
        if(!isReviewOwner(reviewToUpdate)){
            throw new ReviewBadRequestException("Permission denied!");
        }

        // If the last update was 24 hours ago,
        // throw an exception indicating that updates can be performed once an hour.
        boolean cannotEdit = reviewToUpdate.getModifiedDate().isAfter(LocalDateTime.now().minusHours(24));
        if(cannotEdit){
            LocalDateTime mod_plus_24 = reviewToUpdate.getModifiedDate().plusHours(24);

            Duration duration = Duration.between(LocalDateTime.now(), mod_plus_24);

            long seconds = duration.getSeconds();
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;

            String time = String.format("%02d:%02d:%02d", hours, minutes, secs);

            throw new ReviewBadRequestException("You can update your review once every 24 hours. Update available for: " + time);
        }

        // If the new rating isn't empty and differs from the old one, change it.
        if(patchData.getRating() != null && !reviewToUpdate.getRating().equals(patchData.getRating())){
            reviewToUpdate.changeRating(patchData.getRating());
        }

        // If the new comment isn't empty and differs from the old one, change it.
        if(patchData.getComment() != null
                && !patchData.getComment().isEmpty()
                && !reviewToUpdate.getComment().equals(patchData.getComment())){
            reviewToUpdate.changeComment(patchData.getComment());
        }

        // Save changes into db
        Review updatedReview = this.reviewRepository.save(reviewToUpdate);

        // Convert and return
        return ReviewMapper.reviewsToUserReviewResponse(updatedReview);
    }


//    Private methods
    private boolean isReviewOwner(Review review){
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loggedUserId = myUserDetails.getId();
        return review.getUser().getId() == loggedUserId;
    }

}
