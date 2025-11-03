package com.m4zek.backend.model.dto.read;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyOfferResponse {

    private int id;
    private String name;
    private String description;
    private double price;
    private int duration;

}
