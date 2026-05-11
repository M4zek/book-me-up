package com.m4zek.backend.model;


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


    //  METHODS

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public Integer getRating() {
        return rating;
    }

    public CompanyOffer getCompanyOffer() {
        return companyOffer;
    }

    public User getUser() {
        return user;
    }

    public void changeRating(Integer rating) {
        this.rating = rating;
    }

    public void changeComment(String comment) {
        this.comment = comment;
    }
}
