package com.connect.connect.auth.login.exceptions;

public class InvalidCredentialsException extends RuntimeException {
	  private static final long serialVersionUID = -7128386270420622389L;

	  public InvalidCredentialsException() {}

	  public InvalidCredentialsException(String message) {
	    super(message);
	  }

	  public InvalidCredentialsException(Throwable cause) {
	    super(cause);
	  }

	  public InvalidCredentialsException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public InvalidCredentialsException(
	      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	    super(message, cause, enableSuppression, writableStackTrace);
	  }
}
