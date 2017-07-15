package com.inodes.util.mail;

import java.util.Arrays;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.inodes.util.mail.exception.MailerException;
import com.inodes.util.mail.message.MailMessage;
import com.sun.mail.smtp.SMTPTransport;



public class MailSender {
	private static final Logger logger = Logger.getLogger(MailSender.class);


	private Endpoint serverEndpoint;
	private Credentials credentials;


	public MailSender (String host, int port, String user, String pass) {
		this(new Endpoint(host, port), new Credentials(user, pass));
	}

	public MailSender (Endpoint serverEndpoint, Credentials credentials) {
		this.serverEndpoint = serverEndpoint;
		this.credentials = credentials;
	}



	public void send (MailMessage mailMessage) throws MailerException {
		try {
			logger.debug("Getting session");
			Session session = getSession(serverEndpoint.getHost(), serverEndpoint.getPort(), credentials.getUser(), credentials.getPass());

			logger.debug("Creating message");
			MimeMessage message = createMessage(session, mailMessage);
			logger.debug("Saving changes");
			message.saveChanges();

			logger.debug("Getting SMTP transport");
			SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
			try {
				logger.debug("Connecting");
				transport.connect();
				logger.debug("Connected. Sending message");
				transport.sendMessage(message, message.getAllRecipients());
				logger.info("EMail Message sent with code: "+transport.getLastReturnCode());
			} catch (Exception e) {
				logger.error("Could not send EMail Message (Sending exception)", e);
				throw new MailerException(e);
			} finally {
				logger.debug("Closing transport");
				transport.close();
			}
		} catch (MessagingException e) {
			logger.error("Could not send EMail Message (Messaging exception)", e);
			throw new MailerException(e);
		}
	}



	private MimeMessage createMessage (Session session, MailMessage mailMessage) throws MessagingException {
		MimeMessage message = new MimeMessage(session);
		logger.debug("From: "+ mailMessage.getSender());
		message.setFrom(mailMessage.getSender());
		logger.debug("Reply To: "+ mailMessage.getSender());
		message.setReplyTo(new Address[] {mailMessage.getSender()});

		if (mailMessage.getTo().size() > 0) {
			Address[] to = mailMessage.getTo().toArray(new Address[0]);
			logger.debug("To: "+ Arrays.deepToString(to));
			message.setRecipients(RecipientType.TO, to);
		}
		if (mailMessage.getCc().size() > 0) {
			Address[] cc = mailMessage.getCc().toArray(new Address[0]);
			logger.debug("Cc: "+ Arrays.deepToString(cc));
			message.setRecipients(RecipientType.CC, cc);
		}
		if (mailMessage.getCco().size() > 0) {
			Address[] cco = mailMessage.getCco().toArray(new Address[0]);
			logger.debug("Cco: "+ Arrays.deepToString(cco));
			message.setRecipients(RecipientType.BCC, cco);
		}
		logger.debug("Subject: "+ mailMessage.getSubject());
		message.setSubject(mailMessage.getSubject());

		MimeMultipart content = new MimeMultipart();
		MimeBodyPart body = new MimeBodyPart();
		logger.debug("MIME Type: "+ mailMessage.getMimeType());
		logger.debug("Message: "+ mailMessage.getMessage());
		body.setContent(mailMessage.getMessage(), mailMessage.getMimeType());
		content.addBodyPart(body);
		message.setContent(content);

		return message;
	}


	private Session getSession (String host, int port, String user, String pass) {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.port", Integer.toString(port));
		props.setProperty("mail.smtp.user", user);
		props.setProperty("mail.smtp.auth", pass);

		logger.debug("Trying to work with a session with parameters: " +
					 "{'host':'"+props.getProperty("mail.smtp.host")+"'," +
					 " 'port': '"+props.getProperty("mail.smtp.port")+"'," +
					 " 'user': '"+props.getProperty("mail.smtp.user")+"'," +
					 " 'pass': '*****'}");

		Session session;
		try {
			logger.debug("Trying to use default session");
			session = Session.getDefaultInstance(props);
		} catch (SecurityException se) {
			logger.debug("Could not use default session, creating new session");
			session = Session.getInstance(props);
		}
		session.setDebug(false);
		return session;
	}

}
