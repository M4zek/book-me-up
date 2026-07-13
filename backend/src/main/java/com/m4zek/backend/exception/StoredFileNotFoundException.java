package com.m4zek.backend.exception;

public class StoredFileNotFoundException extends RuntimeException {
    public StoredFileNotFoundException(String message) {
        super(message);
    }

    public StoredFileNotFoundException(){
        super("Stored File not found");
    }

}
