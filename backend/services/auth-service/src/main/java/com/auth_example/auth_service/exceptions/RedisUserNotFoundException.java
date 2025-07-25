package com.auth_example.auth_service.exceptions;

public class RedisUserNotFoundException extends RuntimeException {
    public RedisUserNotFoundException(String message) {
        super(message);
    }
}
