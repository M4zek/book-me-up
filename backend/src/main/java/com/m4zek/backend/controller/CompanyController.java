package com.m4zek.backend.controller;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.*;
import com.m4zek.backend.model.dto.write.*;
import com.m4zek.backend.service.CompanyService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(required = false) @Size(min = 1, message = "First name cannot be empty") String firstName,
            @RequestParam(required = false) @Size(min = 1, message = "Last name cannot be empty") String lastName,
            Pageable pageable
    ){
        return ResponseEntity.ok(this.companyService.findEmployeesDetailsByCompanyId(company_id, firstName, lastName, pageable));
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


    /* ----------------------------------------------- *
     *       COMPANY DETAILS MANAGEMENT                *
     * ----------------------------------------------- */

    // Endpoint for update logo or name company
    @PatchMapping(value = "/v1/companies/{companyId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<CompanyDetailsResponse> updateCompanyNameAndLogo(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestPart(value = "data", required = false) @Valid CompanyProfileRequest data,
            @RequestPart(value = "logo", required = false) MultipartFile logo
            ){
        return ResponseEntity.ok(this.companyService.updateCompanyNameOrLogo(companyId, data, logo));
    }


    @PatchMapping("/v1/companies/{companyId}/desc")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<CompanyDetailsResponse> updateCompanyDescriptionAndLogo(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestBody @Valid CompanyDescriptionRequest companyDescriptionRequest
    ){
        return ResponseEntity.ok(this.companyService.updateCompanyDescription(companyId, companyDescriptionRequest));
    }


    @PatchMapping("/v1/companies/{companyId}/address")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<AddressResponse> updateCompanyAddress(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestBody @Valid AddressRequest addressRequest
            ){
        return ResponseEntity.ok(this.companyService.updateCompanyAddress(companyId,addressRequest));
    }


    // --------------- PUBLIC ENDPOINTS ---------------

    // Reading the recommended companies (basic data) based on the best reviews
    @GetMapping("/public/companies/recommended")
    public ResponseEntity<Page<CompanySummaryResponse>> recommendedCompanies(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 2, message = "City name cannot be empty (Min 2 chars)") String city,
            @RequestParam(required = false) @Size(min = 2, message = "Category name cannot be empty (Min 2 chars)") String category
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
