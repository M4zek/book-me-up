package com.m4zek.backend.repository;

import com.m4zek.backend.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            WHERE (:city IS NULL OR LOWER(c.address.city) LIKE LOWER(CONCAT('%', :city, '%')))
                AND (:category IS NULL OR LOWER(c.category.name) LIKE LOWER(CONCAT('%', :category, '%')))
        GROUP BY c
        ORDER BY AVG(r.rating) DESC
    """)
    Page<Company> findAllOrderByAverageRatingDesc(Pageable pageable, @Param("city") String city, @Param("category") String category);

    @Query("""
            SELECT c FROM companies c
            WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:city IS NULL OR LOWER(c.address.city) LIKE LOWER(CONCAT('%', :city, '%')))
              AND (:category IS NULL OR LOWER(c.category.name) LIKE LOWER(CONCAT('%', :category, '%')))
        """)
    Page<Company> searchCompanyByNameAndCityAndCategoryName(
            Pageable pageable,
            @Param("name") String name,
            @Param("city") String city,
            @Param("category") String category
    );

}
