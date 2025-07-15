package com.m4zek.backend.adapter;

import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.repository.CompanyHoursRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlCompanyHoursRepository extends CompanyHoursRepository, JpaRepository<CompanyHours, Integer> {
}
