package com.m4zek.backend.model.projection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyOfferReadModel {

    private String name;
    private String description;
    private double price;
    private int duration;

}
