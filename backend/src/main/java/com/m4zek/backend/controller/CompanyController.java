package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.CompanySummaryResponse;
import com.m4zek.backend.model.dto.write.CompanyRequest;
import com.m4zek.backend.service.CompanyService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // --------------- PRIVATE ENDPOINTS ---------------

    @Transactional
    @PostMapping("/v1/companies")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CompanyDetailsResponse> createNewCompany(
            @RequestBody @Valid CompanyRequest companyRequest)
    {
        CompanyDetailsResponse company = companyService.saveCompany(companyRequest);
        int companyId = company.getId();
        URI location = URI.create("/api/public/companies/" + companyId);
        return ResponseEntity.created(location).body(company);
    }

    // --------------- PUBLIC ENDPOINTS ---------------

    // Reading the recommended companies (basic data) based on the best reviews
    @GetMapping("/public/companies/recommended")
    public ResponseEntity<Page<CompanySummaryResponse>> recommendedCompanies(Pageable pageable) {
        return ResponseEntity.ok(this.companyService.readRecommendedCompany(pageable));
    }

    // Reading detailed company data based on the provided ID
    @GetMapping("/public/companies/{company_id}")
    public ResponseEntity<CompanyDetailsResponse> readCompanyDetails(
            @Positive(message = "Company id must be positive number") @PathVariable int company_id)
    {
        return ResponseEntity.ok(this.companyService.readCompanyDetails(company_id));
    }

    // Search companies by name, city or category
    @GetMapping("/public/companies/search")
    public ResponseEntity<Page<CompanySummaryResponse>> searchCompanies(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "Company name cannot be empty") String companyName,
            @RequestParam(required = false) @Size(min = 1, message = "City name cannot be empty") String city,
            @RequestParam(required = false) @Size(min = 1, message = "Category name cannot be empty") String category
    ) {
        return ResponseEntity.ok(this.companyService.searchCompaniesByNameCityCategory(pageable, companyName, city, category));
    }
}
