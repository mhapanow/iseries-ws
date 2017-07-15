package com.inodes.util.mail.message;

import java.util.List;

import javax.mail.Address;

import com.inodes.util.CollectionFactory;


public abstract class MailMessage {

	private Address sender;
	private List<Address> to;
	private List<Address> cc;
	private List<Address> cco;
	private String subject;

	private String mimeType = "text/html";


	public Address getSender() {
		return sender;
	}
	public MailMessage setSender(Address sender) {
		this.sender = sender;
		return this;
	}

	public List<Address> getTo() {
		return to;
	}
	public MailMessage setTo(List<Address> to) {
		this.to = to;
		return this;
	}
	public MailMessage addTo(Address to) {
		if (null != to) {
			if (null == this.to) {
				this.to = CollectionFactory.createList();
			}
			if (!this.to.contains(to)) {
				this.to.add(to);
			}
		}
		return this;
	}

	public List<Address> getCc() {
		return cc;
	}
	public MailMessage setCc(List<Address> cc) {
		this.cc = cc;
		return this;
	}
	public MailMessage addCc(Address c) {
		if (null != cc) {
			if (null == this.cc) {
				this.cc = CollectionFactory.createList();
			}
			if (!this.cc.contains(c)) {
				this.cc.add(c);
			}
		}
		return this;
	}

	public List<Address> getCco() {
		return cco;
	}
	public MailMessage setCco(List<Address> cco) {
		this.cco = cco;
		return this;
	}
	public MailMessage addCco(Address cco) {
		if (null != cco) {
			if (null == this.cco) {
				this.cco = CollectionFactory.createList();
			}
			if (!this.cco.contains(cco)) {
				this.cco.add(cco);
			}
		}
		return this;
	}

	public String getSubject() {
		return subject;
	}
	public MailMessage setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}


	public abstract String getMessage ();

}
