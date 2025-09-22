package com.m4zek.backend.exception;

public class ReservationExistsException extends RuntimeException {
    public ReservationExistsException(String message) {
        super(message);
    }
}
