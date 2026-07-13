package com.m4zek.backend.repository;

import com.m4zek.backend.model.FileType;
import com.m4zek.backend.model.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoredFileRepository {

    StoredFile save(StoredFile file);

    void deleteByObjectKey(String objectKey);

    boolean existsByCompanyIdAndObjectKey(Long id, String objectKey);

    @Query("""
        SELECT sf
        FROM StoredFile sf
            WHERE sf.id = :imageId
            AND sf.company.id = :companyId
    """)
    Optional<StoredFile> findByIdAndCompanyId(@Param("imageId") int imageId,
                                              @Param("companyId") Long companyId);

    @Query("""
            SELECT CASE
                WHEN COUNT(sf) > 11 THEN true
                ELSE false
            END
            FROM StoredFile sf
            WHERE sf.company.id = :id
                AND sf.type IN :types
    """
    )
    boolean hasCompanyExceededPhotoLimit(@Param("id") Long id,
                                         @Param("types") List<FileType> types);


    @Query("""
        SELECT CASE
            WHEN (COUNT(sf) + :numOfPhotos) > 11 THEN true
            ELSE false
        END
        FROM StoredFile sf
            WHERE sf.company.id = :id
                AND sf.type = FileType.IMG_COMPANY_PORTFOLIO
    """)
    boolean hasCompanySpaceForImages(@Param("id") Long id,
                                     @Param("numOfPhotos") int numOfPhotos);

    @Query("""
        SELECT sf
        FROM StoredFile sf
        WHERE sf.company.id = :id
        AND sf.type = FileType.IMG_COMPANY_LOGO
    """)
    Optional<StoredFile> findCompanyLogo(@Param("id") Long id);


    @Query("""
        SELECT sf
        FROM StoredFile sf
        WHERE sf.company.id = :id
        AND sf.type = FileType.IMG_COMPANY_PORTFOLIO
    """)
    Page<StoredFile> findAllCompanyPortfolioImages(@Param("id") Long id, Pageable pageable);


    @Query("""
        SELECT sf
        FROM StoredFile sf
        WHERE sf.company.id = :id
    """)
    Page<StoredFile> findAllCompanyImages(@Param("id") Long id, Pageable pageable);


    @Query("""
        SELECT sf
        FROM StoredFile sf
        WHERE sf.company.id = :id
            AND sf.type = FileType.IMG_USER_AVATAR
    """)
    Optional<StoredFile> findUserAvatar(@Param("id") Long id);


}
