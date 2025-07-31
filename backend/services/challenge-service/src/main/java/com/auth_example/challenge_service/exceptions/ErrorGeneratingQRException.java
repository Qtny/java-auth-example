package com.auth_example.challenge_service.exceptions;

public class ErrorGeneratingQRException extends RuntimeException {
    public ErrorGeneratingQRException(String message) {
        super(message);
    }
}
