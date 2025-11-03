package com.m4zek.backend.controller;


import com.m4zek.backend.model.dto.read.CompanyOfferResponse;
import com.m4zek.backend.model.dto.write.CompanyOfferRequest;
import com.m4zek.backend.service.CompanyOfferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-offers")
public class CompanyOfferController {


    private final CompanyOfferService companyOfferService;

    public CompanyOfferController(CompanyOfferService companyOfferService) {
        this.companyOfferService = companyOfferService;
    }

    @PostMapping("/{companyId}")
    public ResponseEntity<CompanyOfferResponse> createCompanyOffer(@RequestBody @Valid CompanyOfferRequest companyOffer, @PathVariable int companyId) {

        CompanyOfferResponse companyOfferResponse = this.companyOfferService.createNewOffer(companyOffer, companyId);

        return ResponseEntity.ok(companyOfferResponse);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<List<CompanyOfferResponse>> readAllCompanyOffers(@PathVariable int companyId) {
        return ResponseEntity.ok(this.companyOfferService.getAllCompanyOffers(companyId));
    }


}
