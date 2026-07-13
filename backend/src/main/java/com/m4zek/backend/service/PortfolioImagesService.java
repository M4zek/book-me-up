package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.mapper.ImageMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.StoredFile;
import com.m4zek.backend.model.dto.read.ImageResponse;
import com.m4zek.backend.repository.CompanyRepository;
import com.m4zek.backend.service.facade.FileManagementFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioImagesService {

    private final CompanyRepository companyRepository;
    private final FileManagementFacade storageService;
    private final ImageMapper imageMapper;

    public PortfolioImagesService(CompanyRepository companyRepository, FileManagementFacade fileManagementFacade, ImageMapper imageMapper) {
        this.storageService = fileManagementFacade;
        this.companyRepository = companyRepository;
        this.imageMapper = imageMapper;
    }


    public List<ImageResponse> saveImages(int companyId, List<MultipartFile> portfolioImages) {
        Company company = getCompanyById(companyId);

        List<ImageResponse> imagesResponse = new ArrayList<>();

        for(MultipartFile image: portfolioImages){
            StoredFile file = this.storageService.saveCompanyPortfolioImage(company, image);
            imagesResponse.add(imageMapper.toPortfolioImageResponse(file));
        }

        return imagesResponse;
    }

    public Page<ImageResponse> readImages(int companyId, Pageable pageable) {
        Company company = getCompanyById(companyId);
        Page<StoredFile> images = this.storageService.getCompanyPortfolioImages((long) company.getId(), pageable);

        List<ImageResponse> readModels = images.stream()
                .map(imageMapper::toPortfolioImageResponse)
                .toList();

        return new PageImpl<>(readModels, pageable, images.getTotalElements());
    }

    public void deleteImage(int companyId, String objectKey) {
        this.storageService.deleteFile((long) companyId, objectKey);
    }


    // Private method
    private Company getCompanyById(int companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company with id " + companyId + " not found"));
    }

}
