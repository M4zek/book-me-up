package com.m4zek.backend.mapper;

import com.m4zek.backend.minio.MinioUrlResolver;
import com.m4zek.backend.model.StoredFile;
import com.m4zek.backend.model.dto.read.ImageResponse;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    private final MinioUrlResolver resolver;

    private ImageMapper(MinioUrlResolver resolver) {
        this.resolver = resolver;
    }

    public ImageResponse toPortfolioImageResponse(StoredFile image){
        return ImageResponse.builder()
                .id(image.getId())
                .filename(image.getOriginalFileName())
                .key(image.getObjectKey())
                .imageUrl(resolver.imageUrlMedium(image))
                .type(image.getType())
                .build();
    }


}
