package com.m4zek.backend.mapper;

import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import org.springframework.stereotype.Component;

@Component
public class CompanyOfferMapper {

    private CompanyOfferMapper() {}

    // TO DTO
    public CompanyOfferResponse companyOfferToCompanyOfferResponse(CompanyOffer companyOffer){
        return CompanyOfferResponse.builder()
                .id(companyOffer.getId())
                .name(companyOffer.getName())
                .description(companyOffer.getDescription())
                .price(companyOffer.getPrice())
                .duration(companyOffer.getDuration())
                .build();
    }
}
