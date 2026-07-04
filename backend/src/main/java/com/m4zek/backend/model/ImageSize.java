package com.m4zek.backend.model;

import java.util.List;

public enum ImageSize {
    LARGE,
    MEDIUM,
    ORIGINAL,
    SMALL;


    public static List<ImageSize> types() {
        return List.of(
                LARGE,
                MEDIUM,
                ORIGINAL,
                SMALL
        );
    }
}
