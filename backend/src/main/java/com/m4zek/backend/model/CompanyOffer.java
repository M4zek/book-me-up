package com.m4zek.backend.model;


import com.m4zek.backend.model.projection.CompanyOfferReadModel;
import jakarta.persistence.*;

@Entity(name = "company_offers")
public class CompanyOffer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private double price;
    private int duration;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

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
