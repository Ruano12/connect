package com.connect.connect.auth.login.exceptions;

public class ValidationEmailPendingException extends RuntimeException {
	  private static final long serialVersionUID = -7128386270420622389L;

	  public ValidationEmailPendingException() {}

	  public ValidationEmailPendingException(String message) {
	    super(message);
	  }

	  public ValidationEmailPendingException(Throwable cause) {
	    super(cause);
	  }

	  public ValidationEmailPendingException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public ValidationEmailPendingException(
	      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	    super(message, cause, enableSuppression, writableStackTrace);
	  }
}
