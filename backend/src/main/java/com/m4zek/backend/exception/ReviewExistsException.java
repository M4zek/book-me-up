package com.m4zek.backend.exception;

public class ReviewExistsException extends RuntimeException {
    public ReviewExistsException(String message) {
        super(message);
    }
}
