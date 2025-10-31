package com.m4zek.backend.model;


import com.m4zek.backend.model.projection.CompanyOfferReadModel;
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
    List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "companyOffer", cascade = CascadeType.ALL)
    List<Reservation> reservations = new ArrayList<>();

    public CompanyOffer() {}

    public CompanyOffer(String name, String description, double price, int duration, Company company) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.company = company;
    }

    public CompanyOfferReadModel toReadModel() {
        return CompanyOfferReadModel.builder()
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .duration(this.duration)
                .build();
    }
}
