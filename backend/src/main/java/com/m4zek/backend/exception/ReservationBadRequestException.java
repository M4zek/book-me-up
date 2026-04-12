package com.m4zek.backend.exception;

public class ReservationBadRequestException extends RuntimeException {
    public ReservationBadRequestException(String message) {
        super(message);
    }

}
