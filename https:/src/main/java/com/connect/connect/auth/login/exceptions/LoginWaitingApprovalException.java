package com.connect.connect.auth.login.exceptions;

public class LoginWaitingApprovalException extends RuntimeException {
	  private static final long serialVersionUID = -7128386270420622389L;

	  public LoginWaitingApprovalException() {}

	  public LoginWaitingApprovalException(String message) {
	    super(message);
	  }

	  public LoginWaitingApprovalException(Throwable cause) {
	    super(cause);
	  }

	  public LoginWaitingApprovalException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public LoginWaitingApprovalException(
	      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	    super(message, cause, enableSuppression, writableStackTrace);
	  }
}
