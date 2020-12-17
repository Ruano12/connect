package com.connect.connect.security;


public class InvalidAccessException extends RuntimeException {
  private static final long serialVersionUID = -7128386270420622389L;

  public InvalidAccessException() {}

  public InvalidAccessException(String message) {
    super(message);
  }

  public InvalidAccessException(Throwable cause) {
    super(cause);
  }

  public InvalidAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidAccessException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
