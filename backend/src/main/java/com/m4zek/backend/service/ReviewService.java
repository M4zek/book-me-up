package com.m4zek.backend.service;

import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.exception.ReviewBadRequestException;
import com.m4zek.backend.exception.ReviewExistsException;
import com.m4zek.backend.exception.ReviewNotFoundException;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.write.ReviewPatchRequest;
import com.m4zek.backend.model.dto.write.ReviewRequest;
import com.m4zek.backend.model.projection.ReviewStatisticsProjection;
import com.m4zek.backend.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final static Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    public Page<Review> findCompanyReviews(Pageable pageable, int company_id, Integer rating){
        return this.reviewRepository.findAllByCompanyIdAndRating(company_id, pageable, rating);
    }


    public void isUserAlreadyHaveReview(int authorId, int offerId){
        if(reviewRepository.existsByUserIdAndCompanyOfferId(authorId, offerId)){
            throw new ReviewExistsException("You can only add one review to an offer!");
        }
    }

    public Review createAndSaveReview(User author, CompanyOffer offer, ReviewRequest request){
        // If author don't have reservations throw exception
        if(offer.getReservations().stream()
                .noneMatch(reservation -> reservation.getUser().equals(author))){
            throw new ReviewBadRequestException("Cannot add review without reservation!");
        }

        // Create new entity with review
        Review review = new Review(
                request.getComment(),
                request.getRating(),
                offer,
                author
        );

        // Save new review and return entity
        review = this.save(review);
        return review;
    }

    //    Method for udpating review
    public Review updateReview(Review review, User loggedUser ,ReviewPatchRequest request){

        // If the person calling the endpoint is not the owner of the review
        // , an “Access Denied” exception must be thrown.
        if(!this.isReviewOwner(review, loggedUser)){
            throw new AccessDeniedException("Access denied");
        }

        // If the last update was 24 hours ago,
        // throw an exception indicating that updates can be performed once an hour.
        isReviewCanUpdate(review);

        // If the new rating isn't empty and differs from the old one, change it.
        updateReviewRating(review, request);

        // If the new comment isn't empty and differs from the old one, change it.
        updateReviewComment(review, request);


        // Save changes into db and return saved entity
        review = this.reviewRepository.save(review);
        logger.info("Review [{}] has been updated successfully", review.getId());
        return review;
    }



    public Review save(Review review){
        Review saved = this.reviewRepository.save(review);
        logger.info("Review [{}] has been created by Author [{}]", saved.getId(), saved.getUser().getId());
        return saved;
    }


    public List<Object[]> findCompanyReviewsData(int companyId) {
        return this.reviewRepository.findAllByCompanyId(companyId);
    }

    public ReviewStatisticsProjection findCompanyReviewsStats(int companyId){
        return this.reviewRepository.findReviewStatsByCompanyId(companyId);
    }

    public Review findReviewByIdOrElseThrow(int id){
        return this.reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
    }

    // Private Methods =================

    private boolean isReviewOwner(Review review, User user){
        return review.getUser().getId() == user.getId();
    }


    // Update review rating if request rating is different that rating in review
    private void updateReviewRating(Review review, ReviewPatchRequest request){
        if(request.getRating() != null && !review.getRating().equals(request.getRating())){
            int oldRating = review.getRating();
            review.changeRating(request.getRating());

            logger.info("Review [{}] rating has been changed from [{}] to [{}]", review.getId(), oldRating, review.getRating());
        }
    }

    private void updateReviewComment(Review review, ReviewPatchRequest request){
        if(request.getComment() != null
                && !request.getComment().isEmpty()
                && !review.getComment().equals(request.getComment())){
            String oldComment = review.getComment();
            review.changeComment(request.getComment());

            logger.info("Review [{}] comment has been changed from [{}] to [{}]",
                    review.getId(), oldComment, review.getComment()
            );
        }
    }

    private void isReviewCanUpdate(Review review){
        if(review.getModifiedDate().isAfter(LocalDateTime.now().minusHours(24)))
        {
            LocalDateTime mod_plus_24 = review.getModifiedDate().plusHours(24);

            Duration duration = Duration.between(LocalDateTime.now(), mod_plus_24);

            long seconds = duration.getSeconds();
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;

            String time = String.format("%02d:%02d:%02d", hours, minutes, secs);

            throw new ReviewBadRequestException("You can update your review once every 24 hours. Update available for: " + time);
        }
    }
}
