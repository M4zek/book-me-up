package com.m4zek.backend.repository;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

public interface CompanyOfferRepository {

    Optional<CompanyOffer> findById(long id);

    CompanyOffer save(CompanyOffer companyOffer);

    Page<CompanyOffer> findAllByCompany(Company company, Pageable pageable);

    boolean existsByCompanyAndName(Company company, String name);
}
