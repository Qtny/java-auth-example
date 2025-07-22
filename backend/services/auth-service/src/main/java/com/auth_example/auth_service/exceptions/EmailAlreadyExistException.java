package com.auth_example.auth_service.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
  public EmailAlreadyExistException(String message) {
    super(message);
  }
}
