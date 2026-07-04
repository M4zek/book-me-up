package com.m4zek.backend.controller;


import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;
import com.m4zek.backend.service.facade.CompanyOfferFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CompanyOfferController {


    private final CompanyOfferFacade companyOfferFacade;

    public CompanyOfferController(CompanyOfferFacade companyOfferFacade) {
        this.companyOfferFacade = companyOfferFacade;
    }

    // Private endpoints

    @PostMapping("/v1/company/{companyId}/offers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<CompanyOfferResponse> createCompanyOffer(@RequestBody @Valid CompanyOfferRequest companyOffer, @PathVariable int companyId) {
        CompanyOfferResponse companyOfferResponse = this.companyOfferFacade.createOffer(companyOffer, companyId);
        return ResponseEntity.ok(companyOfferResponse);
    }

    @GetMapping("/v1/company/{companyId}/offers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Page<CompanyOfferResponse>> searchCompanyOffersByName(
            @PathVariable int companyId,
            @RequestParam(required = false) @Size(min = 1, message = "Company offer name cannot be empty") String name,
            Pageable pageable)

    {
        Page<CompanyOfferResponse> offers = this.companyOfferFacade.searchCompanyOfferByName(companyId, name, pageable);
        return ResponseEntity.ok(offers);
    }

    @PatchMapping("/v1/company/{companyId}/offers/{offerId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<CompanyOfferResponse> updateCompanyOffer(
            @PathVariable int companyId,
            @PathVariable int offerId,
            @RequestBody CompanyOfferRequest companyOfferRequest)
    {
        CompanyOfferResponse response = this.companyOfferFacade.updateOffer(companyId, offerId, companyOfferRequest);
        return ResponseEntity.ok(response);
    }




    // Public endpoints

    @GetMapping("/public/company/{companyId}/offers")
    public ResponseEntity<Page<CompanyOfferResponse>> readAllCompanyOffers(@PathVariable int companyId, Pageable pageable) {
        Page<CompanyOfferResponse> offers = this.companyOfferFacade.findCompanyOffer(companyId, pageable);
        return ResponseEntity.ok(offers);
    }


}
