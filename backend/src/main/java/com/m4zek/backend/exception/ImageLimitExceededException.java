package com.m4zek.backend.exception;

public class ImageLimitExceededException extends RuntimeException {
    public ImageLimitExceededException() {
        super("Cannot add more images: the portfolio already contains the maximum of 10 images");
    }

    public ImageLimitExceededException(String message) {
        super(message);
    }
}
