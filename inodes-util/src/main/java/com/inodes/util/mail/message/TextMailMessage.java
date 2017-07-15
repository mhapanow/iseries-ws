package com.inodes.util.mail.message;

public class TextMailMessage extends MailMessage {

	private String text;

	public TextMailMessage (String text) {
		this.text = text;
	}

	@Override
	public String getMessage() {
		return text;
	}
}
