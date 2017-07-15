package com.inodes.util.mail.message;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.inodes.util.CollectionFactory;

public class MailMessageBuilder {

	private static final Pattern ADDRESS_PATTERN = Pattern.compile("([^<]*)\\s*<?([^>]+)*>?");
	private static final String EMAIL_ADDRESS_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


	private Address sender;
	private List<Address> to = CollectionFactory.createList();
	private List<Address> cc = CollectionFactory.createList();
	private List<Address> cco = CollectionFactory.createList();
	private String subject = "";

	private String mimeType = "text/html";

	private String message = "";


	public MailMessageBuilder withSubject (String subject) {
		this.subject = subject;
		return this;
	}

	public MailMessageBuilder withMessagge (String message) {
		this.message = message;
		return this;
	}

	public MailMessageBuilder withMimeType (String mimeType) {
		this.mimeType = mimeType;
		return this;
	}


	public MailMessageBuilder withSender (String sender) throws AddressException {
		List<Address> addressInList = parseAddresses(sender);
		this.sender = null != addressInList && addressInList.size() > 0 ? addressInList.get(0) : null;
		return this;
	}

	public MailMessageBuilder withTo (String to) throws AddressException {
		this.to = parseAddresses(to);
		return this;
	}

	public MailMessageBuilder withCc (String cc) throws AddressException {
		this.cc = parseAddresses(cc);
		return this;
	}

	public MailMessageBuilder withCco (String cco) throws AddressException {
		this.cco = parseAddresses(cco);
		return this;
	}

	private List<Address> parseAddresses (String addressesList) throws AddressException {
		return parseAddresses(CollectionFactory.createList(null != addressesList ? addressesList.split(",") : new String[]{}));
	}

	private List<Address> parseAddresses (List<String> addressesList) throws AddressException {
		List<Address> addresses = new ArrayList<Address>();
		if (null != addressesList) {
			for (int arrayIdx = 0; arrayIdx < addressesList.size(); arrayIdx++) {
				String addressString = addressesList.get(arrayIdx);

				Matcher addressMatcher = ADDRESS_PATTERN.matcher(addressString);
				if (addressMatcher.matches()) {
					addressMatcher.start();

					String name = addressMatcher.groupCount() > 1 && null != addressMatcher.group(2) ? addressMatcher.group(1).trim() : null;
					String address = addressMatcher.groupCount() > 1 && null != addressMatcher.group(2) ? addressMatcher.group(2) : addressMatcher.group(1);

					if (null != address && address.trim().matches(EMAIL_ADDRESS_PATTERN)) {
						try {
							addresses.add(new InternetAddress(address.trim(), name));
						} catch (UnsupportedEncodingException uee) {
							throw new AddressException("Address was malformed ["+addressString+"]");
						}
					}
					else {
						throw new AddressException("Address is invalid ["+addressString+"]");
					}
				}
				else {
					throw new AddressException("Address was malformed ["+addressString+"]");
				}

			}
		}
		return addresses;
	}


	public MailMessage build () {
		MailMessage mailMessage = new TextMailMessage(message);
		mailMessage.setMimeType(mimeType);
		mailMessage.setSubject(subject);
		mailMessage.setSender(sender);
		mailMessage.setTo(to);
		mailMessage.setCc(cc);
		mailMessage.setCco(cco);
		return mailMessage;
	}

}
