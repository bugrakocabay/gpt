package com.example.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect username or password")
public class PasswordException extends Exception{
    public PasswordException(String message) {
        super(message);
    }
}
