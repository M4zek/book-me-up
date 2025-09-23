package com.m4zek.backend.repository;

import com.m4zek.backend.model.CompanyHours;

import java.util.List;

public interface CompanyHoursRepository {
    CompanyHours save(CompanyHours companyHours);

    List<CompanyHours> readAllByCompanyId(Long companyId);
}
