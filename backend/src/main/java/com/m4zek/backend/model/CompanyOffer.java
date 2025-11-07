package com.m4zek.backend.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "company_offers")
public class CompanyOffer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    private double price;
    private int duration;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "companyOffer", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "companyOffer", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public CompanyOffer() {}

    public CompanyOffer(String name, String description, double price, int duration, Company company) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.company = company;
    }

    // ------------ METHODS -------------

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public Company getCompany() {
        return company;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

}
