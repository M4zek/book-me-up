package com.m4zek.backend.service.facade;

import com.m4zek.backend.mapper.ImageMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.StoredFile;
import com.m4zek.backend.model.dto.read.ImageResponse;
import com.m4zek.backend.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CompanyPortfolioFacade {

    private final FileManagementFacade fileManagementFacade;
    private final CompanyService companyService;

    private final ImageMapper imageMapper;

    public CompanyPortfolioFacade(FileManagementFacade fileManagementFacade, CompanyService companyService, ImageMapper imageMapper) {
        this.fileManagementFacade = fileManagementFacade;
        this.companyService = companyService;
        this.imageMapper = imageMapper;
    }


    public List<ImageResponse> uploadPortfolioImages(List<MultipartFile> images, int companyId){
        // Find company
        Company company = this.companyService.findCompanyByIdOrElseThrow(companyId);

        // Save images into database and minio storage
        List<StoredFile> files = this.fileManagementFacade.uploadCompanyPortfolioImages(images, company);

        // Mapped StoredFile into
        return files.stream()
                .map(this.imageMapper::toPortfolioImageResponse)
                .toList();
    }


    public void deleteCompanyPortfolioImage(int imageId, long companyId){

        StoredFile image = this.fileManagementFacade.findCompanyImageOrElseThrow(imageId, companyId);

        String objectKey = image.getObjectKey();


        this.fileManagementFacade.deleteImage(companyId, objectKey);
    }


    // Read all company portfolio
    public Page<ImageResponse> readCompanyPortfolios(long companyId, Pageable pageable){
        Page<StoredFile> images = this.fileManagementFacade.getCompanyPortfolioImages(companyId, pageable);

        List<ImageResponse> response = images.stream()
                .map(this.imageMapper::toPortfolioImageResponse)
                .toList();

        return new PageImpl<>(response, pageable, images.getTotalElements());
    }


}
