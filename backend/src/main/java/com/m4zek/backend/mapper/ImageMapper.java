package com.m4zek.backend.mapper;

import java.util.Base64;

public class ImageMapper {

    private ImageMapper() {}

    public static String byteImageToBase64(byte[] image) {
        return image != null ? Base64.getEncoder().encodeToString(image) : null;
    }

}
