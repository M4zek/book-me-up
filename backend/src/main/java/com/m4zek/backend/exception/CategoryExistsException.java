package com.m4zek.backend.exception;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CategoryExistsException extends EntityExistsException {
    public CategoryExistsException(String message) {
        super(message);
    }
}
