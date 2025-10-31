package com.m4zek.backend.controller;


import com.m4zek.backend.model.dto.read.CompanyOfferReadModel;
import com.m4zek.backend.model.dto.write.CompanyOfferWriteModel;
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
    public ResponseEntity<CompanyOfferReadModel> createCompanyOffer(@RequestBody @Valid CompanyOfferWriteModel companyOffer, @PathVariable int companyId) {

        CompanyOfferReadModel companyOfferReadModel = this.companyOfferService.createNewOffer(companyOffer, companyId);

        return ResponseEntity.ok(companyOfferReadModel);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<List<CompanyOfferReadModel>> readAllCompanyOffers(@PathVariable int companyId) {
        return ResponseEntity.ok(this.companyOfferService.getAllCompanyOffers(companyId));
    }


}
