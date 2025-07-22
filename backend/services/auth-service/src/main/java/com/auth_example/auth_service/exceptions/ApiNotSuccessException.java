package com.auth_example.auth_service.exceptions;

public class ApiNotSuccessException extends RuntimeException {
  public ApiNotSuccessException(String message) {
    super(message);
  }
}
