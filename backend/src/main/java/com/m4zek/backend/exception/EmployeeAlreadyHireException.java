package com.m4zek.backend.exception;

public class EmployeeAlreadyHireException extends RuntimeException {
    public EmployeeAlreadyHireException(String message) {
        super(message);
    }
}
