package com.connect.connect.auth.login.exceptions;

public class TechnicalException extends RuntimeException {
	  
	private static final long serialVersionUID = -7128386270420622389L;

	public TechnicalException() {}

	public TechnicalException(String message) {
		super(message);
	}

	public TechnicalException(Throwable cause) {
		super(cause);
	}

	public TechnicalException(String message, Throwable cause) {
		super(message, cause);
	}

	public TechnicalException(
			String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
