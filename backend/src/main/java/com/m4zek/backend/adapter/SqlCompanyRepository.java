package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.repository.CompanyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlCompanyRepository extends CompanyRepository, JpaRepository<Company, Integer> {
}
