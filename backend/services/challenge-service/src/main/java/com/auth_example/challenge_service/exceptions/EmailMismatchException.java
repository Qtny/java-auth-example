package com.auth_example.challenge_service.exceptions;

public class EmailMismatchException extends RuntimeException {
    public EmailMismatchException(String message) {
        super(message);
    }
}
