package com.auth_example.user_service.exceptions;

public class MfaNotSetupException extends RuntimeException {
    public MfaNotSetupException(String message) {
        super(message);
    }
}
