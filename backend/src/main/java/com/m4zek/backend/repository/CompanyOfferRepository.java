package com.m4zek.backend.repository;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

public interface CompanyOfferRepository {

    Optional<CompanyOffer> findByIdAndCompanyId(long id, int companyId);

    Optional<CompanyOffer> findById(long id);

    CompanyOffer save(CompanyOffer companyOffer);

    Page<CompanyOffer> findAllByCompany(Company company, Pageable pageable);

    boolean existsByCompanyAndName(Company company, String name);

    @Query("""
        SELECT o from company_offers o
            WHERE o.company.id = :companyId
                AND  (:name IS NULL OR LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%')))
        """)
    Page<CompanyOffer> searchCompanyOffersByCompanyIdAndName(int companyId, String name, Pageable pageable);

    Optional<CompanyOffer> findByIdAndCompany(int id, Company company);
}
