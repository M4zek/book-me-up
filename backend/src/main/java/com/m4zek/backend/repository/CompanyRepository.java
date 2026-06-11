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
    SELECT c FROM companies c
    ORDER BY c.averageRating DESC
    """)
    Page<Company> findAll(Pageable pageable);



    @Query(value = """
            SELECT c.*
            FROM companies c
            JOIN addresses a ON a.company_id = c.id
            JOIN categories cat ON cat.id = c.category_id
            WHERE (:name IS NULL OR MATCH(c.name) AGAINST(CONCAT(:name, '*') IN BOOLEAN MODE))
              AND (:city IS NULL OR MATCH(a.city) AGAINST(CONCAT(:city, '*') IN BOOLEAN MODE))
              AND (:category IS NULL OR MATCH(cat.name) AGAINST(CONCAT(:category, '*') IN BOOLEAN MODE))
    """,
        countQuery = """
            SELECT COUNT(*)
            FROM companies c
            JOIN addresses a ON a.company_id = c.id
            JOIN categories cat ON cat.id = c.category_id
            WHERE (:name IS NULL OR MATCH(c.name) AGAINST(CONCAT(:name, '*') IN BOOLEAN MODE))
              AND (:city IS NULL OR MATCH(a.city) AGAINST(CONCAT(:city, '*') IN BOOLEAN MODE))
              AND (:category IS NULL OR MATCH(cat.name) AGAINST(CONCAT(:category, '*') IN BOOLEAN MODE))
        """,
        nativeQuery = true)
    Page<Company> searchCompanyByNameAndCityAndCategoryName(
            Pageable pageable,
            @Param("name") String name,
            @Param("city") String city,
            @Param("category") String category
    );





// Improvement for searching company by category, cities or both.

    @Query(value = """
        SELECT c.*
        FROM companies c
        JOIN addresses a ON a.company_id = c.id
        WHERE MATCH(a.city) AGAINST(CONCAT(:city, '*') IN BOOLEAN MODE)
        ORDER BY c.average_rating DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM companies c
        JOIN addresses a ON a.company_id = c.id
        WHERE MATCH(a.city) AGAINST(CONCAT(:city, '*') IN BOOLEAN MODE)
        """,
            nativeQuery = true
    )
    Page<Company> findByCity(String city, Pageable pageable);


    @Query(value = """
        SELECT c.*
        FROM companies c
        JOIN categories cat ON cat.id = c.category_id
        WHERE MATCH(cat.name) AGAINST(CONCAT(:category, '*') IN BOOLEAN MODE)
        ORDER BY c.average_rating DESC
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM companies c
        JOIN categories cat ON cat.id = c.category_id
        WHERE MATCH(cat.name) AGAINST(CONCAT(:category, '*') IN BOOLEAN MODE)
    """, nativeQuery = true)
    Page<Company> findByCategory(String category, Pageable pageable);


    @Query(value = """
        SELECT c.*
        FROM companies c
        JOIN addresses a ON a.company_id = c.id
        JOIN categories cat ON cat.id = c.category_id
        WHERE MATCH(a.city) AGAINST(CONCAT(:city, '*') IN BOOLEAN MODE)
          AND MATCH(cat.name) AGAINST(CONCAT(:category, '*') IN BOOLEAN MODE)
        ORDER BY c.average_rating DESC
        """, nativeQuery = true)
    Page<Company> findByCityAndCategory(String city, String category, Pageable pageable);

}