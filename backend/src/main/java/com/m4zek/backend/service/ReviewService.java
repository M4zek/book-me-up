package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.ReviewExistsException;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.ReviewReadModel;
import com.m4zek.backend.model.dto.write.ReviewWriteModel;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.ReviewRepository;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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


    public ReviewReadModel createNewReview(ReviewWriteModel reviewWriteModel) {

        if(reviewRepository.existsByUserIdAndCompanyOfferId(reviewWriteModel.getAuthor_id(), reviewWriteModel.getCompany_offer_id())){
            throw new ReviewExistsException("You can only add one review to an offer!");
        }

        User author = userRepository.findById(reviewWriteModel.getAuthor_id())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        CompanyOffer companyOffer = companyOfferRepository.findById(reviewWriteModel.getCompany_offer_id())
                .orElseThrow(() -> new CompanyNotFoundException("Company offer not found"));

        Review savedReview = this.reviewRepository.save(new Review(
                reviewWriteModel.getComment(),
                reviewWriteModel.getRating(),
                companyOffer,
                author
        ));

        return savedReview.toReadModel();
    }

    public List<ReviewReadModel> readAllCompanyOfferReviews(long company_offer_id) {
        return this.reviewRepository.readAllByCompanyOfferId(company_offer_id)
                .stream()
                .map(Review::toReadModel)
                .collect(Collectors.toList());
    }

}
