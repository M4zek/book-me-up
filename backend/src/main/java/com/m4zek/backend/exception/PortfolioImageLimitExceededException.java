package com.m4zek.backend.exception;

public class PortfolioImageLimitExceededException extends RuntimeException {
    public PortfolioImageLimitExceededException() {
        super("Cannot add more images: the portfolio already contains the maximum of 10 images");
    }

    public PortfolioImageLimitExceededException(String message) {
        super(message);
    }
}
