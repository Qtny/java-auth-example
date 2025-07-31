package com.auth_example.challenge_service.exceptions;

public class TotpProfileNotFoundException extends RuntimeException {
    public TotpProfileNotFoundException(String message) {
        super(message);
    }
}
