package com.m4zek.backend.controller;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.CompanySummaryResponse;
import com.m4zek.backend.model.dto.read.EmployeeDetailsResponse;
import com.m4zek.backend.model.dto.read.EmployeeSummaryResponse;
import com.m4zek.backend.model.dto.write.CompanyRequest;
import com.m4zek.backend.model.dto.write.EmployeeHireRequest;
import com.m4zek.backend.model.dto.write.UserCompanyRoleRequest;
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
import java.util.List;

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

    @GetMapping("/v1/companies/{company_id}/employees")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<EmployeeSummaryResponse>> findEmployeesByCompanyId(@Positive(message = "Company id must be positive number") @PathVariable int company_id){
        return ResponseEntity.ok(this.companyService.findEmployeesByCompanyId(company_id));
    }

    @GetMapping("/v1/companies/{company_id}/employees/details")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<EmployeeDetailsResponse>> findEmployeesDetailsByCompanyId(
            @Positive(message = "Company id must be positive number") @PathVariable int company_id,
            Pageable pageable
    ){
        return ResponseEntity.ok(this.companyService.findEmployeesDetailsByCompanyId(company_id, pageable));
    }

    // Endpoint for changing user role in company
    @PatchMapping("/v1/companies/{companyId}/employees/{employeeId}/role")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<EmployeeDetailsResponse> updateEmployeeRole(
            @PathVariable int companyId,
            @PathVariable int employeeId,
            @RequestBody @Valid UserCompanyRoleRequest userCompanyRoleRequest
    ){
        return ResponseEntity.ok(this.companyService.updateEmployeeRoleInCompany(companyId, employeeId, userCompanyRoleRequest));
    }

    // Endpoint for employee dismissal
    @DeleteMapping("/v1/companies/{companyId}/employee/{employeeId}/dismiss")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<?> dismissEmployee(@PathVariable int companyId, @PathVariable int employeeId){
        this.companyService.dismissEmployeeFromCompany(companyId, employeeId);
        return ResponseEntity.ok().build();
    }

    // Endpoint for hiring new employees
    @PostMapping("/v1/companies/{companyId}/employees/hire")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<List<EmployeeDetailsResponse>> hireEmployees(
            @PathVariable int companyId,
            @Valid @RequestBody EmployeeHireRequest employeeHireRequest
    ){
        return  ResponseEntity.ok(this.companyService.hireEmployeeToCompany(companyId, employeeHireRequest));
    }
    // --------------- PUBLIC ENDPOINTS ---------------

    // Reading the recommended companies (basic data) based on the best reviews
    @GetMapping("/public/companies/recommended")
    public ResponseEntity<Page<CompanySummaryResponse>> recommendedCompanies(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "City name cannot be empty") String city,
            @RequestParam(required = false) @Size(min = 1, message = "Category name cannot be empty") String category
    ) {
        return ResponseEntity.ok(this.companyService.readRecommendedCompany(pageable, city, category));
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
