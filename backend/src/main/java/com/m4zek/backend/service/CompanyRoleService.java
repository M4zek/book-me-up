package com.m4zek.backend.service;


import com.m4zek.backend.repository.CompanyUserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyRoleService {

    private final CompanyUserRoleRepository repository;

    public CompanyRoleService(CompanyUserRoleRepository repository) {
        this.repository = repository;
    }

    public boolean hasAnyRoleInCompany(Integer company_id, Integer user_id, List<String> roleNames) {
        return this.repository.existsByUserIdAndCompanyIdAndRoleNameIn(user_id, company_id, roleNames);
    }
}
