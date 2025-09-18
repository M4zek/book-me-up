package com.m4zek.backend.repository;

import com.m4zek.backend.model.CompanyRole;

import java.util.Optional;

public interface CompanyRoleRepository {
    Optional<CompanyRole> findById(int company_id);

    Optional<CompanyRole> findByName(String role_name);
}
