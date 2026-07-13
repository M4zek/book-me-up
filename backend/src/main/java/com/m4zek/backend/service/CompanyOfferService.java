package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;
import com.m4zek.backend.repository.CompanyOfferRepository;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyOfferService {

    private final static Logger logger = LoggerFactory.getLogger(CompanyOfferService.class);

    private final CompanyOfferRepository companyOfferRepository;

    public CompanyOfferService(CompanyOfferRepository companyOfferRepository) {
        this.companyOfferRepository = companyOfferRepository;
    }

    public CompanyOffer createAndSave(Company company, CompanyOfferRequest request){
        CompanyOffer offer = new CompanyOffer(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getDuration(),
                company);
        offer = this.save(offer);
        return offer;
    }

    public CompanyOffer save(CompanyOffer offer){
        CompanyOffer savedOffer = this.companyOfferRepository.save(offer);
        logger.info("Company offer [{}] has been saved. Company owner -> [{}]",
                savedOffer.getId(), savedOffer.getCompany().getId());
        return savedOffer;
    }

    public CompanyOffer updateOffer(CompanyOfferRequest req, CompanyOffer offer) {

        Optional.ofNullable(req.getName()).ifPresent(offer::setName);
        Optional.ofNullable(req.getDescription()).ifPresent(offer::setDescription);
        Optional.ofNullable(req.getPrice()).filter(p -> p != 0.0).ifPresent(offer::setPrice);
        Optional.ofNullable(req.getDuration()).filter(d -> d != 0).ifPresent(offer::setDuration);

        offer = this.companyOfferRepository.save(offer);
        logger.info("Company offer [{}] has been updated", offer.getId());
        return offer;
    }

    public Page<CompanyOffer> searchCompanyOfferByName(String name, int companyId, Pageable pageable){
        return this.companyOfferRepository.searchCompanyOffersByCompanyIdAndName(companyId, name, pageable);
    }

    public Optional<CompanyOffer> findCompanyOffer(long companyOfferId){
        return this.companyOfferRepository.findById(companyOfferId);
    }


    public Page<CompanyOffer> findCompanyOffers(Company company, Pageable pageable){
        return this.companyOfferRepository.findAllByCompany(company, pageable);
    }

    public CompanyOffer findCompanyOffer(Company company, int offerId){
        return this.companyOfferRepository.findByIdAndCompany(offerId,company)
                .orElseThrow(() -> new CompanyNotFoundException("Company offer not found"));
    }

    public void offerExistsInCompany(Company company, CompanyOfferRequest request){
        if(this.companyOfferRepository.existsByCompanyAndName(company, request.getName())) {
            throw new EntityExistsException("Company Offer with name " + request.getName() + " already exists");
        }
    }

}
