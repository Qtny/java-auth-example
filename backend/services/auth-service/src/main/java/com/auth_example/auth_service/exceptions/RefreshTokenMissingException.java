package com.auth_example.auth_service.exceptions;

public class RefreshTokenMissingException extends RuntimeException {
    public RefreshTokenMissingException(String message) {
        super(message);
    }
}
