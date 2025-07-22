package com.auth_example.common_service.exceptions;

public class ServerErrorException extends RuntimeException {
  public ServerErrorException(String message) {
    super(message);
  }
}
