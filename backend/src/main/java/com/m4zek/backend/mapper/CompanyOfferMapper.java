package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;

public class CompanyOfferMapper {

    private CompanyOfferMapper() {}


    // TO DTO
    public static CompanyOfferResponse companyOfferToCompanyOfferResponse(CompanyOffer companyOffer){
        return CompanyOfferResponse.builder()
                .id(companyOffer.getId())
                .name(companyOffer.getName())
                .description(companyOffer.getDescription())
                .price(companyOffer.getPrice())
                .duration(companyOffer.getDuration())
                .build();
    }


    // TO Entity
    public static CompanyOffer companyOfferRequestToCompanyOffer(CompanyOfferRequest request, Company company){
        return new CompanyOffer(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getDuration(),
                company);
    }
}
