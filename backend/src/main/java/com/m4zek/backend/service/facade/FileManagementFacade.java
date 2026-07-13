package com.m4zek.backend.service.facade;


import com.m4zek.backend.exception.ImageLimitExceededException;
import com.m4zek.backend.exception.StorageException;
import com.m4zek.backend.exception.StoredFileNotFoundException;
import com.m4zek.backend.minio.MinioKeyGenerator;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.FileType;
import com.m4zek.backend.model.StoredFile;
import com.m4zek.backend.service.MinIOStorageService;
import com.m4zek.backend.service.StoredFileService;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class FileManagementFacade {

    private final StoredFileService storedFileService;
    private final MinIOStorageService minIOStorageService;


    public FileManagementFacade(StoredFileService storedFileService, MinIOStorageService minIOStorageService) {
        this.storedFileService = storedFileService;
        this.minIOStorageService = minIOStorageService;
    }



    @Transactional
    public List<StoredFile> uploadCompanyPortfolioImages(List<MultipartFile> files, Company company){
        // Check that the company has space (file size plus current files < 11) If more throw exception
        if(this.storedFileService.hasCompanySpaceForPortfolio(company.getId(), files.size()))
            throw new ImageLimitExceededException("The limit for the company's image has been exceeded");

        // Try save all images
        List<StoredFile> savedFiles = files.stream()
                .map(file -> saveCompanyPortfolioImage(company, file))
                .toList();

        return savedFiles;
    }


    @Transactional
    public StoredFile saveCompanyPortfolioImage(Company company, MultipartFile file) {
        if(storedFileService.hasCompanyExceededPhotoLimit((long) company.getId()))
            throw new ImageLimitExceededException("The limit for the company's image has been exceeded");

        String objectKey = MinioKeyGenerator.companyPortfolio((long) company.getId(), file.getOriginalFilename());

        // Save Images into database
        StoredFile image = this.storedFileService.createStoredFile(objectKey, file, FileType.IMG_COMPANY_PORTFOLIO);
        image.assignToCompany(company);

        // Upload images into MinIO
        this.minIOStorageService.uploadImage(file, objectKey);

        return this.storedFileService.saveStoredFile(image);
    }

    public StoredFile saveCompanyLogo(Long id, MultipartFile file){
        // Find current company logo
        Optional<StoredFile> companyLogoOptional = this.storedFileService.findCompanyLogo(id);

        // Generate new company logo object key
        String newObjectKey = MinioKeyGenerator.companyLogo(id, file.getOriginalFilename());

        // If company has logo, update it else create new one
        if(companyLogoOptional.isPresent()){
            // Get current stored file with logo data
            StoredFile currentLogo = companyLogoOptional.get();

            // Get old logo key
            String oldObjectKey = currentLogo.getObjectKey();

            // Insert new image to MinIO
            this.minIOStorageService.uploadImage(file, newObjectKey);

            // Remove old image from MinIO (4 version - 4 sizes)
            this.minIOStorageService.deleteImage(oldObjectKey);

            // Set up new logo key
            currentLogo.updateObjectKey(newObjectKey);

            // Save stored file with new object key
            return this.storedFileService.updateFile(currentLogo);
        }

        // Upload file into MinIO
        this.minIOStorageService.uploadImage(file, newObjectKey);

        // Save and return StoredFile (New Company logo data)
        return this.storedFileService.saveImageFile(newObjectKey, file, FileType.IMG_COMPANY_LOGO);
    }

    public Page<StoredFile> getCompanyPortfolioImages(Long companyId, Pageable pageable){
        return this.storedFileService.findCompanyImagePortfolio(companyId, pageable);
    }

    public StoredFile findCompanyImageOrElseThrow(int imageId, long companyId){
        return this.storedFileService.findByIdAndCompanyId(imageId, companyId)
                .orElseThrow(() -> new StoredFileNotFoundException("Company image not found"));
    }

    @Transactional
    public void deleteImage(long companyId, String objectKey){
        if(!this.storedFileService.isFileExists(companyId, objectKey)){
            throw new StoredFileNotFoundException("File not found");
        } else {
            minIOStorageService.deleteImage(objectKey);
            storedFileService.deleteFile(objectKey);
        }
    }

    @Transactional
    public StoredFile saveFile(String objectKey, MultipartFile file, FileType type) {
        minIOStorageService.uploadImage(file, objectKey);
        return storedFileService.saveImageFile(objectKey, file, type);
    }

    @Transactional
    public void deleteFile(Long companyId, String objectKey) {
        try{
            if(this.storedFileService.isFileExists(companyId, objectKey)){
                minIOStorageService.deleteFile(objectKey);
                storedFileService.deleteFile(objectKey);
            } else {
                throw new StoredFileNotFoundException("File not found.");
            }
        } catch (MinioException ex){
            throw new StorageException(ex);
        } catch (StoredFileNotFoundException e){
            throw e;
        }
    }

    @Transactional
    public void assignLogoIfPresentToCompany(Company company, MultipartFile logo){
        if (logo == null || logo.isEmpty()) {
            return;
        }

        try {
            StoredFile file = this.saveCompanyLogo((long) company.getId(), logo);
            file.assignToCompany(company);
            company.assignLogoFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
