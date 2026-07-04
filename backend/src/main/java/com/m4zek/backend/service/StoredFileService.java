package com.m4zek.backend.service;


import com.m4zek.backend.minio.MinioKeyGenerator;
import com.m4zek.backend.model.FileType;
import com.m4zek.backend.model.StoredFile;
import com.m4zek.backend.repository.StoredFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Service
public class StoredFileService {

    private final StoredFileRepository fileRepository;

    public StoredFileService(StoredFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public StoredFile createStoredFile(String objectKey, MultipartFile file, FileType fileType){
        String fileName = MinioKeyGenerator.changeIMGExtensionToWEBP(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        return new StoredFile(
                objectKey,
                fileName,
                file.getContentType(),
                file.getSize(),
                fileType
        );
    }

    public StoredFile saveStoredFile(StoredFile storedFile){
        return this.fileRepository.save(storedFile);
    }

    public StoredFile saveImageFile(String objectKey, MultipartFile file, FileType fileType) {
        String fileName = MinioKeyGenerator.changeIMGExtensionToWEBP(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        StoredFile image = new StoredFile(
                objectKey,
                fileName,
                file.getContentType(),
                file.getSize(),
                fileType
        );

        return fileRepository.save(image);
    }

    // Method to delete data about file from db based on objectKey
    @Transactional
    public void deleteFile(String objectKey){
        fileRepository.deleteByObjectKey(objectKey);
    }

    public StoredFile updateFile(StoredFile storedFile){
        return this.fileRepository.save(storedFile);
    }

    public boolean hasCompanyExceededPhotoLimit(Long id){
        return fileRepository.hasCompanyExceededPhotoLimit(id, FileType.imageTypes());
    }

    public boolean hasCompanySpaceForPortfolio(long id, int numOfPhotos){
        return fileRepository.hasCompanySpaceForImages(id, numOfPhotos);
    }

    public Optional<StoredFile> findCompanyLogo(Long id){
        return this.fileRepository.findCompanyLogo(id);
    }

    public Page<StoredFile> findCompanyImages(Long id, Pageable pageable){
        return this.fileRepository.findAllCompanyImages(id, pageable);
    }

    public Page<StoredFile> findCompanyImagePortfolio(Long id, Pageable pageable){
        return this.fileRepository.findAllCompanyPortfolioImages(id, pageable);
    }

    public boolean isFileExists(Long companyId, String objectKey){
        return this.fileRepository.existsByCompanyIdAndObjectKey(companyId, objectKey);
    }

    public Optional<StoredFile> findByIdAndCompanyId(int imageId, Long companyId) {
        return this.fileRepository.findByIdAndCompanyId(imageId, companyId);
    }
}