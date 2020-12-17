package com.connect.connect.auth.login.exceptions;

public class AccountDisabledException extends RuntimeException {
	  private static final long serialVersionUID = -7128386270420622389L;

	  public AccountDisabledException() {}

	  public AccountDisabledException(String message) {
	    super(message);
	  }

	  public AccountDisabledException(Throwable cause) {
	    super(cause);
	  }

	  public AccountDisabledException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public AccountDisabledException(
	      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	    super(message, cause, enableSuppression, writableStackTrace);
	  }
}
