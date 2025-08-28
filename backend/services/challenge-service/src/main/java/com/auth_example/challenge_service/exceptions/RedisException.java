package com.auth_example.challenge_service.exceptions;

public class RedisException extends RuntimeException {
    public RedisException(String message) {
        super(message);
    }
}
