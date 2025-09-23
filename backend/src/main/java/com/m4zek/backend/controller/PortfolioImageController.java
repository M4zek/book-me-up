package com.m4zek.backend.controller;

import com.m4zek.backend.model.projection.PortfolioImageReadModel;
import com.m4zek.backend.service.PortfolioImagesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio-images")
public class PortfolioImageController {

    private final PortfolioImagesService portfolioImagesService;

    public PortfolioImageController(PortfolioImagesService portfolioImagesService) {
        this.portfolioImagesService = portfolioImagesService;
    }

    @PostMapping("/{companyId}")
    public ResponseEntity<List<PortfolioImageReadModel>> uploadNewImage(
            @PathVariable("companyId") int companyId,
            List<MultipartFile> images)
    {
        List<PortfolioImageReadModel> portfolioImageReadModels =
                this.portfolioImagesService.saveImages(companyId, images);

        return ResponseEntity.ok().body(portfolioImageReadModels);
    }


    @GetMapping("/{companyId}")
    public ResponseEntity<Page<PortfolioImageReadModel>> getPortfolioImages(
            @PathVariable("companyId") int companyId, Pageable pageable)
    {
        return ResponseEntity.ok().body(this.portfolioImagesService.readImages(companyId, pageable));
    }


    @DeleteMapping("/{imageId}")
    public ResponseEntity<HttpStatus> removeImage(@PathVariable("imageId") int imageId){
        this.portfolioImagesService.deleteImage(imageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/{imageId}/download")
    public ResponseEntity<byte[]> downloadImage(@PathVariable("imageId") int imageId) {
        PortfolioImageReadModel readModel = this.portfolioImagesService.getImageById(imageId);
        byte[] image = readModel.getImage();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("attachment", readModel.getFilename());

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
