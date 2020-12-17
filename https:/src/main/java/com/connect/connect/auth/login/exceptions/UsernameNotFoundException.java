package com.connect.connect.auth.login.exceptions;

public class UsernameNotFoundException extends RuntimeException {
	  private static final long serialVersionUID = -7128386270420622389L;

	  public UsernameNotFoundException() {}

	  public UsernameNotFoundException(String message) {
	    super(message);
	  }

	  public UsernameNotFoundException(Throwable cause) {
	    super(cause);
	  }

	  public UsernameNotFoundException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public UsernameNotFoundException(
	      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	    super(message, cause, enableSuppression, writableStackTrace);
	  }
}
