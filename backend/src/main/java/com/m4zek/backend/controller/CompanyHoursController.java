package com.m4zek.backend.controller;


import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.CompanyHoursResponse;
import com.m4zek.backend.model.dto.write.CompanyHoursRequest;
import com.m4zek.backend.service.facade.CompanyHoursFacade;
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

    private final CompanyHoursFacade companyHoursFacade;

    public CompanyHoursController(CompanyHoursFacade companyHoursFacade) {
        this.companyHoursFacade = companyHoursFacade;
    }

    @PostMapping("/{companyId}/hours")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<CompanyHoursResponse>> addCompanyHours(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestBody @Valid List<CompanyHoursRequest> companyHours) {

        List<CompanyHoursResponse> response = this.companyHoursFacade.createCompanyBusinessHours(companyId, companyHours);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{companyId}/hours")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<CompanyHoursResponse>> readCompanyHours(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId) {
        List<CompanyHoursResponse> response = this.companyHoursFacade.readCompanyBusinessHours(companyId);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{companyId}/opening-hours")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<List<CompanyHoursResponse>> updateCompanyHours(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestBody @Valid List<CompanyHoursRequest> companyHours
    )
    {
        List<CompanyHoursResponse> response = this.companyHoursFacade.updateCompanyHours(companyId, companyHours);
        return ResponseEntity.ok(response);
    }


}
