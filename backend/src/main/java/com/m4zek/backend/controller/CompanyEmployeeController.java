package com.m4zek.backend.controller;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.EmployeeDetailsResponse;
import com.m4zek.backend.model.dto.read.EmployeeSummaryResponse;
import com.m4zek.backend.model.dto.read.UserCompanyResponse;
import com.m4zek.backend.model.dto.write.EmployeeHireRequest;
import com.m4zek.backend.model.dto.write.UserCompanyRoleRequest;
import com.m4zek.backend.service.facade.UserCompanyFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CompanyEmployeeController {

    private final UserCompanyFacade userCompanyFacade;

    public CompanyEmployeeController(UserCompanyFacade userCompanyFacade) {
        this.userCompanyFacade = userCompanyFacade;
    }


    // Endpoint for reading all log-in user companies
    @GetMapping("/users/me/companies")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<UserCompanyResponse>> findAllUserCompany(){
        List<UserCompanyResponse> response = this.userCompanyFacade.findUserCompanies();
        return ResponseEntity.ok(response);
    }

    // Endpoint for reading all company employees (basic data)
    @GetMapping("/companies/{company_id}/employees")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<EmployeeSummaryResponse>> findEmployeesByCompanyId(@Positive(message = "Company id must be positive number") @PathVariable int company_id){
        List<EmployeeSummaryResponse> response = this.userCompanyFacade.findCompanyEmployees(company_id);
        return ResponseEntity.ok(response);
    }

    // Endpoint for reading all company employees (details data)
    @GetMapping("/companies/{company_id}/employees/details")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<EmployeeDetailsResponse>> findEmployeesDetailsByCompanyId(
            @Positive(message = "Company id must be positive number") @PathVariable int company_id,
            @RequestParam(required = false) @Size(min = 1, message = "First name cannot be empty") String firstName,
            @RequestParam(required = false) @Size(min = 1, message = "Last name cannot be empty") String lastName,
            Pageable pageable
    ){

        Page<EmployeeDetailsResponse> responses = this.userCompanyFacade.findCompanyEmployeesDetails(
                company_id, firstName, lastName, pageable
        );

        return ResponseEntity.ok(responses);
    }

    // Endpoint for update user role in company
    @PatchMapping("/companies/{companyId}/employees/{employeeId}/role")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<EmployeeDetailsResponse> updateEmployeeRole(
            @PathVariable int companyId,
            @PathVariable int employeeId,
            @RequestBody @Valid UserCompanyRoleRequest userCompanyRoleRequest
    ){
        EmployeeDetailsResponse response = this.userCompanyFacade.changeEmployeeRoleInCompany(companyId, employeeId, userCompanyRoleRequest);
        return ResponseEntity.ok(response);
    }

    // Endpoint for dismiss employee from company
    @DeleteMapping("/companies/{companyId}/employees/{employeeId}/dismiss")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<?> dismissEmployee(@PathVariable int companyId, @PathVariable int employeeId){
        this.userCompanyFacade.dismissEmployee(employeeId, companyId);
        return ResponseEntity.ok().build();
    }

    // Endpoint for hire employee to company
    @PostMapping("/companies/{companyId}/employees/hire")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<List<EmployeeDetailsResponse>> hireEmployees(
            @PathVariable int companyId,
            @Valid @RequestBody EmployeeHireRequest employeeHireRequest
    ){
        return  ResponseEntity.ok(this.userCompanyFacade.hireEmployee(companyId, employeeHireRequest));
    }

}
