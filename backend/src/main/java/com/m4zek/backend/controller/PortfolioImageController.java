package com.m4zek.backend.controller;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.PortfolioImage;
import com.m4zek.backend.model.dto.read.PortfolioImageResponse;
import com.m4zek.backend.service.PortfolioImagesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PortfolioImageController {

    private final PortfolioImagesService portfolioImagesService;

    public PortfolioImageController(PortfolioImagesService portfolioImagesService) {
        this.portfolioImagesService = portfolioImagesService;
    }


    // Private endpoints
    @PostMapping("/v1/companies/{companyId}/portfolio-images")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<List<PortfolioImageResponse>> uploadNewImage(
            @PathVariable("companyId") int companyId,
            List<MultipartFile> images)
    {
        List<PortfolioImageResponse> portfolioImageResponses =
                this.portfolioImagesService.saveImages(companyId, images);

        return ResponseEntity.ok().body(portfolioImageResponses);
    }


    @DeleteMapping("/v1/companies/{companyId}/portfolio-images/{imageId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_MANAGER"})
    public ResponseEntity<HttpStatus> removeImage(
            @PathVariable("companyId") int companyId,
            @PathVariable("imageId") int imageId){
        this.portfolioImagesService.deleteImage(imageId, companyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/v1/companies/portfolio-images/{imageId}/download")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> downloadImage(@PathVariable("imageId") int imageId) {
        PortfolioImage readModel = this.portfolioImagesService.getImageById(imageId);
        byte[] image = readModel.getImage();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("attachment", readModel.getFilename());

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }


    // Public endpoints
    @GetMapping("/public/companies/{companyId}/portfolio-images")
    public ResponseEntity<Page<PortfolioImageResponse>> getPortfolioImages(
            @PathVariable("companyId") int companyId, Pageable pageable)
    {
        return ResponseEntity.ok().body(this.portfolioImagesService.readImages(companyId, pageable));
    }


}
