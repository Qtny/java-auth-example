package com.auth_example.challenge_service.exceptions;

public class CodeDoesNotMatchException extends RuntimeException {
    public CodeDoesNotMatchException(String message) {
        super(message);
    }
}
