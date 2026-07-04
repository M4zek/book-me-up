package com.m4zek.backend.controller;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.ImageResponse;
import com.m4zek.backend.service.facade.CompanyPortfolioFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PortfolioImageController {

    private final CompanyPortfolioFacade portfolioFacade;

    public PortfolioImageController(CompanyPortfolioFacade portfolioFacade) {
        this.portfolioFacade = portfolioFacade;
    }


    // Private endpoints
    @PostMapping("/v1/companies/{companyId}/portfolio-images")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<List<ImageResponse>> uploadNewImage(
            @PathVariable("companyId") int companyId,
            List<MultipartFile> images)
    {
        List<ImageResponse> imageRespons =
                this.portfolioFacade.uploadPortfolioImages(images, companyId);

        return ResponseEntity.ok().body(imageRespons);
    }


    @DeleteMapping("/v1/companies/{companyId}/portfolio-images/{imageId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<HttpStatus> removeImage(
            @PathVariable("companyId") int companyId,
            @PathVariable("imageId") int imageId){
        this.portfolioFacade.deleteCompanyPortfolioImage(imageId, companyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    // Public endpoints
    // Read company portfolio
    @GetMapping("/public/companies/{companyId}/portfolio-images")
    public ResponseEntity<Page<ImageResponse>> getPortfolioImages(
            @PathVariable("companyId") int companyId, Pageable pageable)
    {
        Page<ImageResponse> response = this.portfolioFacade.readCompanyPortfolios(companyId, pageable);
        return ResponseEntity.ok().body(response);
    }


}
