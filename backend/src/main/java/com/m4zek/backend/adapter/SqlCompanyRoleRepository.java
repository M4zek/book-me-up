package com.m4zek.backend.adapter;

import com.m4zek.backend.model.CompanyRole;
import com.m4zek.backend.repository.CompanyRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlCompanyRoleRepository extends CompanyRoleRepository, JpaRepository<CompanyRole, Integer> {
}
