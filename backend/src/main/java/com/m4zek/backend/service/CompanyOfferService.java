package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.mapper.CompanyOfferMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.CompanyRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyOfferService {

    private final CompanyOfferRepository companyOfferRepository;
    private final CompanyRepository companyRepository;

    public CompanyOfferService(CompanyOfferRepository companyOfferRepository, CompanyRepository companyRepository) {
        this.companyOfferRepository = companyOfferRepository;
        this.companyRepository = companyRepository;
    }


    public Page<CompanyOfferResponse> getAllCompanyOffers(int companyId, Pageable pageable) {
        Company company = getCompanyById(companyId);

        Page<CompanyOffer> companyOffers = companyOfferRepository.findAllByCompany(company, pageable);
        List<CompanyOfferResponse> companyOfferResponseList = companyOffers.stream()
                .map(CompanyOfferMapper::companyOfferToCompanyOfferResponse)
                .toList();

        return new PageImpl<>(companyOfferResponseList, pageable, companyOffers.getTotalElements());
    }


    public CompanyOfferResponse createNewOffer(CompanyOfferRequest companyOfferRequest, int companyId) {
        Company company = getCompanyById(companyId);

        if(this.companyOfferRepository.existsByCompanyAndName(company, companyOfferRequest.getName())) {
            throw new EntityExistsException("Company Offer with name " + companyOfferRequest.getName() + " already exists");
        }

        CompanyOffer newOffer = CompanyOfferMapper.companyOfferRequestToCompanyOffer(companyOfferRequest, company);
        CompanyOffer savedCompanyOffer = companyOfferRepository.save(newOffer);
        return CompanyOfferMapper.companyOfferToCompanyOfferResponse(savedCompanyOffer);
    }


    // Private method
    private Company getCompanyById(int companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company with id " + companyId + " not found"));
    }
}
