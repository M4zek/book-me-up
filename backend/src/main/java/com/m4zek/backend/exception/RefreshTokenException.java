package com.m4zek.backend.exception;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String token, String message){
        super("Failed for (" + token + "): " + message);
    }
}
