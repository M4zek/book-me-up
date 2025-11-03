package com.m4zek.backend.repository;

import com.m4zek.backend.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompanyRepository {
    Company save(Company company);

    Optional<Company> findById(long id);

    void delete(Company company);

    @Query("""
        SELECT c
        FROM companies c
        LEFT JOIN c.companyOffers o
        LEFT JOIN o.reviews r
        GROUP BY c
        ORDER BY AVG(r.rating) DESC
    """)
    Page<Company> findAllOrderByAverageRatingDesc(Pageable pageable);

    Page<Company> findCompaniesByNameContainingOrAddressCityContainingOrCategoryNameContaining(
            Pageable pageable,
            String name,
            String city,
            String category
    );

}
