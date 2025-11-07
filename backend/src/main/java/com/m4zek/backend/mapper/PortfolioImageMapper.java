package com.m4zek.backend.mapper;

import com.m4zek.backend.model.PortfolioImage;
import com.m4zek.backend.model.dto.read.PortfolioImageResponse;

public class PortfolioImageMapper {

    private PortfolioImageMapper() {}

    public static PortfolioImageResponse portfolioImageToPortfolioImageResponse(PortfolioImage portfolioImage) {
        return PortfolioImageResponse.builder()
                .id(portfolioImage.getId())
                .filename(portfolioImage.getFilename())
                .downloadUrl("/api/v1/companies/portfolio-images/"+ portfolioImage.getId() +"/download")
                .image(ImageMapper.byteImageToBase64(portfolioImage.getImage()))
                .build();
    }


}
