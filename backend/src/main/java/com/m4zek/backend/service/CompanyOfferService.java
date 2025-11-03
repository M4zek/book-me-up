package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyOfferService {

    private final CompanyOfferRepository companyOfferRepository;
    private final CompanyRepository companyRepository;

    public CompanyOfferService(CompanyOfferRepository companyOfferRepository, CompanyRepository companyRepository) {
        this.companyOfferRepository = companyOfferRepository;
        this.companyRepository = companyRepository;
    }


    public List<CompanyOfferResponse> getAllCompanyOffers(int companyId) {
        Company company = getCompanyById(companyId);
        List<CompanyOffer> companyOffers = companyOfferRepository.findAllByCompany(company);
        return companyOffers.stream().map(CompanyOffer::toReadModel).collect(Collectors.toList());
    }


    public CompanyOfferResponse createNewOffer(CompanyOfferRequest companyOfferRequest, int companyId) {
        Company company = getCompanyById(companyId);
        CompanyOffer newOffer = companyOfferRequest.toEntity(company);
        return this.companyOfferRepository.save(newOffer).toReadModel();
    }


    // Private method
    private Company getCompanyById(int companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company with id " + companyId + " not found"));
    }
}
