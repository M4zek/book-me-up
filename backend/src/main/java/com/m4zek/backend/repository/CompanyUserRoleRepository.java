package com.m4zek.backend.repository;

import com.m4zek.backend.model.CompanyUserRole;

import java.util.List;
import java.util.Optional;

public interface CompanyUserRoleRepository {
    CompanyUserRole save(CompanyUserRole companyUserRole);

    Optional<CompanyUserRole> findByUserIdAndCompanyId(int user_id, int company_id);

    Optional<CompanyUserRole> findByCompanyId(int company_id);

    List<CompanyUserRole> findAllByCompanyId(int userId);

    List<CompanyUserRole> findAllByUserId(int userId);
}
