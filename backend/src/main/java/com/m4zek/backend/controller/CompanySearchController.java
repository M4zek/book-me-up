package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.CompanySummaryResponse;
import com.m4zek.backend.service.facade.CompanySearchFacade;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/companies")
public class CompanySearchController {

    private final CompanySearchFacade companySearchFacade;

    public CompanySearchController(CompanySearchFacade companySearchFacade) {
        this.companySearchFacade = companySearchFacade;
    }

    @GetMapping("{company_id}")
    public ResponseEntity<CompanyDetailsResponse> readCompanyDetails(
            @Positive(message = "Company id must be positive number") @PathVariable int company_id)
    {
        CompanyDetailsResponse response = this.companySearchFacade.findCompany(company_id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommended")
    public ResponseEntity<Page<CompanySummaryResponse>> recommendedCompanies(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 2, message = "City name cannot be empty (Min 2 chars)") String city,
            @RequestParam(required = false) @Size(min = 2, message = "Category name cannot be empty (Min 2 chars)") String category
    ) {
        Page<CompanySummaryResponse> response = this.companySearchFacade.searchRecommendedCompanies(
                pageable, city, category
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<CompanySummaryResponse>> searchCompanies(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "Company name cannot be empty") String companyName,
            @RequestParam(required = false) @Size(min = 1, message = "City name cannot be empty") String city,
            @RequestParam(required = false) @Size(min = 1, message = "Category name cannot be empty") String category
    ) {
        Page<CompanySummaryResponse> response = this.companySearchFacade.searchCompanies(
                pageable, companyName, city, category
        );

        return ResponseEntity.ok(response);
    }
}
