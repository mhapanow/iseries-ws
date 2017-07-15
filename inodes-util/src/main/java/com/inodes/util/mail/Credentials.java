package com.inodes.util.mail;

public class Credentials {
	private String user;
	private String pass;

	public Credentials(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}

	public String getPass() {
		return pass;
	}
	public Credentials setPass(String pass) {
		this.pass = pass;
		return this;
	}

	public String getUser() {
		return user;
	}
	public Credentials setUser(String user) {
		this.user = user;
		return this;
	}
}
