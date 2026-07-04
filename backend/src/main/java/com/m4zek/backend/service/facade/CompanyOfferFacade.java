package com.m4zek.backend.service.facade;


import com.m4zek.backend.mapper.CompanyOfferMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;
import com.m4zek.backend.service.CompanyOfferService;
import com.m4zek.backend.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyOfferFacade {

    private final CompanyService companyService;
    private final CompanyOfferService companyOfferService;

    private final CompanyOfferMapper companyOfferMapper;

    public CompanyOfferFacade(CompanyService companyService, CompanyOfferService companyOfferService, CompanyOfferMapper companyOfferMapper) {
        this.companyService = companyService;
        this.companyOfferService = companyOfferService;
        this.companyOfferMapper = companyOfferMapper;
    }


    // Create new company offer
    public CompanyOfferResponse createOffer(CompanyOfferRequest request, int companyId){
        // Find company
        Company company = this.companyService.findCompanyByIdOrElseThrow(companyId);

        // Check if company have offer (same offer name in company) throw exception Offer already exists in company
        this.companyOfferService.offerExistsInCompany(company, request);

        // Create and save offer
        CompanyOffer savedOffer = this.companyOfferService.createAndSave(company, request);

        // Return mapped offer
        return this.companyOfferMapper.companyOfferToCompanyOfferResponse(savedOffer);
    }

    // Method to update offer
    public CompanyOfferResponse updateOffer(int companyId, int offerId, CompanyOfferRequest request){
        // Find company offer
        Company company = this.companyService.findCompanyByIdOrElseThrow(companyId);

        // Search offer by company and offer id
        CompanyOffer offer = this.companyOfferService.findCompanyOffer(company, offerId);

        // Update offer
        offer = this.companyOfferService.updateOffer(request, offer);

        // Return mapped offer
        return this.companyOfferMapper.companyOfferToCompanyOfferResponse(offer);
    }


    // Read company offers
    public Page<CompanyOfferResponse> findCompanyOffer(int companyId, Pageable pageable){

        // Find company
        Company company = this.companyService.findCompanyByIdOrElseThrow(companyId);
        // Find company offers based on company
        Page<CompanyOffer> offers = this.companyOfferService.findCompanyOffers(company, pageable);
        // Mapped offers to response
        List<CompanyOfferResponse> mappedOffers = offers.stream()
                .map(this.companyOfferMapper::companyOfferToCompanyOfferResponse)
                .toList();


        return new PageImpl<>(mappedOffers, pageable, offers.getTotalElements());
    }


    // Search company offer by name
    public Page<CompanyOfferResponse> searchCompanyOfferByName(int companyId, String name, Pageable pageable){
        // Find company offers by name
        Page<CompanyOffer> offers = this.companyOfferService.searchCompanyOfferByName(name, companyId, pageable);
        // Mapped offer into Response DTO.
        List<CompanyOfferResponse> mappedOffers = offers.stream()
                .map(this.companyOfferMapper::companyOfferToCompanyOfferResponse)
                .toList();

        return new PageImpl<>(mappedOffers, pageable, offers.getTotalElements());
    }



}
