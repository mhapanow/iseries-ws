package com.inodes.util.mail.exception;

public class MailerException extends Exception {
	private static final long serialVersionUID = 3234323428297896008L;

	public MailerException(String message) {
		super(message);
	}

	public MailerException(Throwable cause) {
		super(cause);
	}

	public MailerException(String message, Throwable cause) {
		super(message, cause);
	}
}
