package com.m4zek.backend.model;

import java.util.List;

public enum FileType {
    IMG_USER_AVATAR,
    IMG_COMPANY_LOGO,
    IMG_COMPANY_PORTFOLIO;


    public static List<FileType> imageTypes() {
        return List.of(
                IMG_USER_AVATAR,
                IMG_COMPANY_LOGO,
                IMG_COMPANY_PORTFOLIO
        );
    }
}
