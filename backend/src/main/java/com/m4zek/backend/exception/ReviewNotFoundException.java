package com.m4zek.backend.exception;

public class ReviewNotFoundException extends RuntimeException{

    public ReviewNotFoundException(String message){
        super(message);
    }

    public ReviewNotFoundException(){
        super("Review not fond");
    }

}
