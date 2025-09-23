package com.m4zek.backend.advice;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


public record ErrorMessage(
        int statusCode,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date timestamp,
        Object message,
        String description) {

}
