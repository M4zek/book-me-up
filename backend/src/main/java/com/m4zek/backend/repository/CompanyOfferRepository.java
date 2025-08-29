package com.m4zek.backend.repository;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;

import java.util.List;
import java.util.Optional;

public interface CompanyOfferRepository {

    Optional<CompanyOffer> findById(int id);

    CompanyOffer save(CompanyOffer companyOffer);

    List<CompanyOffer> findAllByCompany(Company company);
}
