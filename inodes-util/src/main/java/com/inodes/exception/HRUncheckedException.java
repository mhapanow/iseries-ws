package com.inodes.exception;

public class HRUncheckedException extends RuntimeException {
	private static final long serialVersionUID = 2499383536544344060L;

	public HRUncheckedException(String message) {
		super(message);
	}

	public HRUncheckedException(String message, Throwable cause) {
		super(message, cause);
	}

}
