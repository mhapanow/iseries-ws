package com.inodes.exception;

public class ReflectiveOperationException extends HRUncheckedException {
	private static final long serialVersionUID = 8977974419251017004L;

	public ReflectiveOperationException(String message) {
		super(message);
	}

	public ReflectiveOperationException(String message, Throwable cause) {
		super(message, cause);
	}

}
