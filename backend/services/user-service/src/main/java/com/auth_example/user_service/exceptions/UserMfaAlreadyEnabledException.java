package com.auth_example.user_service.exceptions;

public class UserMfaAlreadyEnabledException extends RuntimeException {
    public UserMfaAlreadyEnabledException(String message) {
        super(message);
    }
}
