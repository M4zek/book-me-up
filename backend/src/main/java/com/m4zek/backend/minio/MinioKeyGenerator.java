package com.m4zek.backend.minio;

import com.m4zek.backend.model.ImageSize;

import java.util.UUID;

public final class MinioKeyGenerator {

    public static final String USERS = "users";
    public static final String AVATAR = "avatar";

    public static final String COMPANIES = "companies";
    public static final String LOGO = "logo";
    public static final String PORTFOLIO = "portfolio";

    private MinioKeyGenerator(){}

    public static String userAvatar(Long userId, String fileName){
        return String.format("%s/%d/%s/%s-%s",
                USERS, userId,
                AVATAR, UUID.randomUUID(),
                MinioKeyGenerator.changeIMGExtensionToWEBP(fileName));
    }

    public static String companyLogo(Long companyId, String fileName){
        return String.format("%s/%d/%s/%s-%s",
                COMPANIES, companyId,
                LOGO, UUID.randomUUID(),
                MinioKeyGenerator.changeIMGExtensionToWEBP(fileName));
    }

    public static String companyPortfolio(Long companyId, String fileName){
        return String.format("%s/%d/%s/%s-%s",
                COMPANIES, companyId,
                PORTFOLIO, UUID.randomUUID(),
                MinioKeyGenerator.changeIMGExtensionToWEBP(fileName));
    }


    // ----------------------------------------------------
    // ----------- Method to operating on files -----------
    // ----------------------------------------------------

    // Change name (extension) file to .webp
    public static String changeIMGExtensionToWEBP(String fileName){
        return fileName.replaceAll("(?i)\\.(jpg|jpeg|png)$", ".webp");
    }

    // Add image size to objectKey ex.: ...[MEDIUM].webp
    public static String addSizeToImageObjectKey(String key, ImageSize size){
        return key.replaceAll("(?i)\\.(webp)$", "[" + size.name() + "].webp");
    }

}
