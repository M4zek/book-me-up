package com.m4zek.backend.controller;


import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.CompanyHoursResponse;
import com.m4zek.backend.model.dto.write.CompanyHoursRequest;
import com.m4zek.backend.service.CompanyHoursService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/companies")
public class CompanyHoursController {

    private final CompanyHoursService companyHoursService;

    public CompanyHoursController(CompanyHoursService companyHoursService) {
        this.companyHoursService = companyHoursService;
    }

    @PostMapping("/{companyId}/hours")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public List<CompanyHoursResponse> addCompanyHours(
            @Positive(message = "Company id must be positive number") @PathVariable Long companyId,
            @RequestBody @Valid List<CompanyHoursRequest> companyHours) {
        return this.companyHoursService.setCompanyHours(companyId, companyHours);
    }

    @GetMapping("/{companyId}/hours")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public List<CompanyHoursResponse> readCompanyHours(@Positive(message = "Company id must be positive number") @PathVariable Long companyId) {
        return this.companyHoursService.readCompanyHours(companyId);
    }


    @PatchMapping("/{companyId}/opening-hours")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<List<CompanyHoursResponse>> updateCompanyHours(
            @Positive(message = "Company id must be positive number") @PathVariable Long companyId,
            @RequestBody @Valid List<CompanyHoursRequest> companyHours
    )
    {
        return ResponseEntity.ok(this.companyHoursService.updateCompanyOpeningHours(companyId, companyHours));
    }


}
