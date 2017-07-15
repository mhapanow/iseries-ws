package com.inodes.util.mail;

import javax.mail.internet.AddressException;

import com.inodes.util.mail.exception.MailerException;
import com.inodes.util.mail.message.MailMessageBuilder;

public class MailSenderFacade {


	public static void sendMail (String host, int port, String user, String pass, String from, String to, String cc, String cco, String subject, String message) throws MailerException {
		try {
			MailMessageBuilder builder = new MailMessageBuilder();
			builder.withSender(from).withTo(to).withCc(cc).withCco(cco).withSubject(subject).withMessagge(message);

			MailSender sender = new MailSender(host, port, user, pass);
			sender.send(builder.build());
		} catch (AddressException ae) {
			throw new MailerException(ae);
		}
	}
}
