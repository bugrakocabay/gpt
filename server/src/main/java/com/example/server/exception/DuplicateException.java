package com.example.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Duplicate key error")
public class DuplicateException extends Exception{
    public DuplicateException(String message) {
        super(message);
    }
}
