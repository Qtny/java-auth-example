package com.auth_example.common_service.exceptions;

public class ClientErrorException extends RuntimeException {
    public ClientErrorException(String message) {
        super(message);
    }
}
