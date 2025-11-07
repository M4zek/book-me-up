package com.m4zek.backend.controller;


import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;
import com.m4zek.backend.service.CompanyOfferService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CompanyOfferController {


    private final CompanyOfferService companyOfferService;

    public CompanyOfferController(CompanyOfferService companyOfferService) {
        this.companyOfferService = companyOfferService;
    }

    // Private endpoints

    @PostMapping("/v1/company/{companyId}/offers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    // TODO Insert your own annotation checking that only Owner or Manager company can add a new offer
    public ResponseEntity<CompanyOfferResponse> createCompanyOffer(@RequestBody @Valid CompanyOfferRequest companyOffer, @PathVariable int companyId) {
        CompanyOfferResponse companyOfferResponse = this.companyOfferService.createNewOffer(companyOffer, companyId);
        return ResponseEntity.ok(companyOfferResponse);
    }


    // Public endpoints

    @GetMapping("/public/company/{companyId}/offers")
    public ResponseEntity<Page<CompanyOfferResponse>> readAllCompanyOffers(@PathVariable int companyId, Pageable pageable) {
        return ResponseEntity.ok(this.companyOfferService.getAllCompanyOffers(companyId, pageable));
    }


}
