package com.m4zek.backend.model.dto.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySummaryResponse {

    private int id;
    private String name;
    private AddressResponse address;
    private double rating;
    private int numberOfReviews;
    private byte[] logo;

}
