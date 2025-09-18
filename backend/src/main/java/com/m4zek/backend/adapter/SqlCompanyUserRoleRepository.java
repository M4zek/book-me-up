package com.m4zek.backend.adapter;

import com.m4zek.backend.model.CompanyUserRole;
import com.m4zek.backend.repository.CompanyUserRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlCompanyUserRoleRepository extends CompanyUserRoleRepository, JpaRepository<CompanyUserRole, Integer> {
}
