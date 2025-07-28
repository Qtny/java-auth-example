package com.auth_example.auth_service.exceptions;

public class InvalidEmailPasswordException extends RuntimeException {
    public InvalidEmailPasswordException(String message) {
        super(message);
    }
}
