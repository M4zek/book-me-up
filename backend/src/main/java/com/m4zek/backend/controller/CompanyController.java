package com.m4zek.backend.controller;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.write.CompanyDescriptionRequest;
import com.m4zek.backend.model.dto.write.CompanyProfileRequest;
import com.m4zek.backend.model.dto.write.CompanyRequest;
import com.m4zek.backend.service.facade.CompanyFacade;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api")
@Validated
public class CompanyController {

    private final CompanyFacade companyFacade;

    public CompanyController(CompanyFacade companyFacade) {
        this.companyFacade = companyFacade;
    }

    // --------------- PRIVATE ENDPOINTS ---------------
    @Transactional
    @PostMapping(
            value = "/v1/companies",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CompanyDetailsResponse> createNewCompany(
            @RequestPart("companyRequest") @Valid CompanyRequest companyRequest,
            @RequestPart(value = "file", required = false) MultipartFile file)
    {
        CompanyDetailsResponse company = companyFacade.createCompany(companyRequest, file);
        int companyId = company.getId();
        URI location = URI.create("/api/public/companies/" + companyId);
        return ResponseEntity.created(location).body(company);
    }



    /* ----------------------------------------------- *
     *       COMPANY DETAILS MANAGEMENT                *
     * ----------------------------------------------- */

//    // Endpoint for update logo or name company
    @PatchMapping(value = "/v1/companies/{companyId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<CompanyDetailsResponse> updateCompanyNameAndLogo(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestPart(value = "data", required = false) @Valid CompanyProfileRequest data,
            @RequestPart(value = "logo", required = false) MultipartFile logo
            ){

        CompanyDetailsResponse response = this.companyFacade.updateCompanyProfile(
                companyId,
                data,
                logo
        );

        return ResponseEntity.ok(response);
    }

    // Endpoint for updating company description
    @PatchMapping("/v1/companies/{companyId}/desc")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER"})
    public ResponseEntity<CompanyDetailsResponse> updateCompanyDescriptionAndLogo(
            @Positive(message = "Company id must be positive number") @PathVariable int companyId,
            @RequestBody @Valid CompanyDescriptionRequest companyDescriptionRequest
    ){
        CompanyDetailsResponse response = this.companyFacade.updateCompanyDetails(
                companyId,
                companyDescriptionRequest
        );

        return ResponseEntity.ok(response);
    }

}
