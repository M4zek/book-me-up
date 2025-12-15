package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.PortfolioImageLimitExceededException;
import com.m4zek.backend.exception.ImageException;
import com.m4zek.backend.exception.ImageNotFoundException;
import com.m4zek.backend.mapper.PortfolioImageMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.PortfolioImage;
import com.m4zek.backend.model.dto.read.PortfolioImageResponse;
import com.m4zek.backend.repository.CompanyRepository;
import com.m4zek.backend.repository.PortfolioImageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PortfolioImagesService {

    private final PortfolioImageRepository portfolioImageRepository;
    private final CompanyRepository companyRepository;

    public PortfolioImagesService(PortfolioImageRepository portfolioImageRepository, CompanyRepository companyRepository) {
        this.portfolioImageRepository = portfolioImageRepository;
        this.companyRepository = companyRepository;
    }


    public List<PortfolioImageResponse> saveImages(int companyId, List<MultipartFile> portfolioImages) {
        Company company = getCompanyById(companyId);

        if (company.getPortfolioImagesSize() >= 10){
            throw new PortfolioImageLimitExceededException();
        }

         return portfolioImages.stream()
                .map(image -> {
                    try {
                        String filename = image.getOriginalFilename();
                        byte[] imageBytes = image.getBytes();

                        return PortfolioImageMapper.portfolioImageToPortfolioImageResponse(
                                portfolioImageRepository.save(
                                        new PortfolioImage(imageBytes, filename, company)
                                )
                        );
                    } catch (IOException exception){
                        throw new ImageException(exception.getMessage());
                    }
                }).toList();
    }

    public Page<PortfolioImageResponse> readImages(int companyId, Pageable pageable) {
        Company company = getCompanyById(companyId);
        Page<PortfolioImage> images = portfolioImageRepository.findAllByCompany(company, pageable);

        List<PortfolioImageResponse> readModels = images.stream()
                .map(PortfolioImageMapper::portfolioImageToPortfolioImageResponse)
                .toList();

        return new PageImpl<>(readModels, pageable, images.getTotalElements());
    }

    public void deleteImage(int imageId, int companyId) {
        PortfolioImage portfolioImage = portfolioImageRepository.findByIdAndCompanyId(imageId, companyId)
                .orElseThrow(() -> new ImageNotFoundException("Image with id " + imageId + " not found"));
        portfolioImageRepository.delete(portfolioImage);
    }

    public PortfolioImage getImageById(int imageId) {
       return portfolioImageRepository.findById(imageId)
               .orElseThrow(() -> new ImageNotFoundException("Image with id " + imageId + " not found"));
    }

    // Private method
    private Company getCompanyById(int companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company with id " + companyId + " not found"));
    }

}
