package com.m4zek.backend.model;


import com.m4zek.backend.model.dto.read.ReviewReadModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity(name = "reviews")
public class Review extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Min(1)
    @Max(5)
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "company_offer_id")
    private CompanyOffer companyOffer;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Review() {}

    public Review(String comment, Integer rating, CompanyOffer companyOffer, User user) {
        this.comment = comment;
        this.rating = rating;
        this.companyOffer = companyOffer;
        this.user = user;
    }

    public ReviewReadModel toReadModel() {
        return ReviewReadModel.builder()
                .id(this.id)
                .comment(this.comment)
                .rating(this.rating)
                .author_name(this.user.toUserReadModel().getFirstName())
                .build();
    }

}
