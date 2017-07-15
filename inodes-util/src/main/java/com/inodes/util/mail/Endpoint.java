package com.inodes.util.mail;

public class Endpoint {
	private String host;
	private int port;

	public Endpoint(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}
	public Endpoint setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}
	public Endpoint setPort(int port) {
		this.port = port;
		return this;
	}
}
