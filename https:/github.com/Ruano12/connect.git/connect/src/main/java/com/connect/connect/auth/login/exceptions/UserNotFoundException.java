package com.connect.connect.auth.login.exceptions;

public class UserNotFoundException extends RuntimeException {
	  private static final long serialVersionUID = -7128386270420622389L;

	  public UserNotFoundException() {}

	  public UserNotFoundException(String message) {
	    super(message);
	  }

	  public UserNotFoundException(Throwable cause) {
	    super(cause);
	  }

	  public UserNotFoundException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public UserNotFoundException(
	      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	    super(message, cause, enableSuppression, writableStackTrace);
	  }
}
