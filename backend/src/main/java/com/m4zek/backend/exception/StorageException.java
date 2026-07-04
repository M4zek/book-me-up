package com.m4zek.backend.exception;

public class StorageException extends RuntimeException{

    public StorageException(Throwable cause) {
        super(cause);
    }

    public StorageException(String message){
        super(message);
    }

}
