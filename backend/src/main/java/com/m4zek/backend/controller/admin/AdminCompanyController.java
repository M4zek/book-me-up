package com.m4zek.backend.controller.admin;

import com.m4zek.backend.model.dto.admin.CompanyRankingResponse;
import com.m4zek.backend.service.facade.admin.AdminCompanyFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/companies")
public class AdminCompanyController {

    private final AdminCompanyFacade companyFacade;

    public AdminCompanyController(AdminCompanyFacade companyFacade) {
        this.companyFacade = companyFacade;
    }

    @GetMapping("/popular")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<Page<CompanyRankingResponse>> getTopCompaniesByReservations(Pageable pageable){
        Page<CompanyRankingResponse> response = this.companyFacade.getTopCompaniesBasedOnReservation(pageable);
        return ResponseEntity.ok(response);
    }
}
