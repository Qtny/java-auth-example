package com.auth_example.auth_service.exceptions;

public class TokenCreationException extends RuntimeException {
  public TokenCreationException(String message) {
    super(message);
  }
}
