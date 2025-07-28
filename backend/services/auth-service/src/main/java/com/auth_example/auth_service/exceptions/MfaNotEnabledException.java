package com.auth_example.auth_service.exceptions;

public class MfaNotEnabledException extends RuntimeException {
    public String token;
    public MfaNotEnabledException(String message, String token) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
