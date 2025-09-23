package com.m4zek.backend.repository;

import com.m4zek.backend.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CompanyRepository {
    Company save(Company company);

    Optional<Company> findById(long id);

    Page<Company> findAll(Pageable pageable);

    void delete(Company company);
}
