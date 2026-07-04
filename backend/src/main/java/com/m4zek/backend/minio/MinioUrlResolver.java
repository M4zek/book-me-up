package com.m4zek.backend.minio;


import com.m4zek.backend.exception.StorageException;
import com.m4zek.backend.model.ImageSize;
import com.m4zek.backend.model.StoredFile;
import com.m4zek.backend.service.MinIOStorageService;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Component;

@Component
public class MinioUrlResolver {

    private final MinIOStorageService service;

    public MinioUrlResolver(MinIOStorageService service) {
        this.service = service;
    }


    public String imageUrl(StoredFile file, ImageSize size) {
        try{
            if (file == null) {
                return null;
            }
            return service.generatePresignedURL(
                    file.getObjectKey(),
                    size
            );
        } catch (MinioException e){
             throw new StorageException(e);
        }


    }

    public String imageUrlMedium(StoredFile file) {
        return imageUrl(file, ImageSize.MEDIUM);
    }

    public String imageUrlSmall(StoredFile file) {
        return imageUrl(file, ImageSize.SMALL);
    }

    public String imageUrlLarge(StoredFile file){
        return imageUrl(file, ImageSize.LARGE);
    }


}
