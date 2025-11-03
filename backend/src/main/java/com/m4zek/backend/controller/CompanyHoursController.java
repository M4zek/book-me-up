package com.m4zek.backend.controller;


import com.m4zek.backend.model.dto.read.CompanyHoursResponse;
import com.m4zek.backend.model.dto.write.CompanyHoursRequest;
import com.m4zek.backend.service.CompanyHoursService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/company-hours")
public class CompanyHoursController {

    private final CompanyHoursService companyHoursService;

    public CompanyHoursController(CompanyHoursService companyHoursService) {
        this.companyHoursService = companyHoursService;
    }

    @PostMapping("/{companyId}")
    public List<CompanyHoursResponse> addCompanyHours(
            @PathVariable Long companyId,
            @RequestBody @Valid List<CompanyHoursRequest> companyHours) {
        return this.companyHoursService.setCompanyHours(companyId, companyHours);
    }

    @GetMapping("/{companyId}")
    public List<CompanyHoursResponse> readCompanyHours(@PathVariable Long companyId) {
        return this.companyHoursService.readCompanyHours(companyId);
    }


}
