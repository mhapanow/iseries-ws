package com.inodes.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyFileLoader {

	private String path;
	private Properties properties;

	/**
	 * Parameterized constructor
	 * @param path
	 * @throws IOException
	 */
	public PropertyFileLoader(String path) throws IOException {
		System.out.println("Configuration file: " + path);
		this.path = path;
		initialize();
	}
	
	/**
	 * This method loads the properties
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		properties = new Properties();
		properties.load(FileLoader.getResource(path, FileLoader.PRECEDENCE_SYSTEMPATH).openStream());
	}

	public String getPath() {
		return path;
	}
	
	/**
	 * This method returns the value according to the key
	 * or null if the the property is not mapped
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		try {
			if(properties == null)
				initialize();
			return properties.getProperty(key);
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * This method returns the value according to the key
	 * as integer
	 * @param key
	 * @return
	 */
	public int getPropertyAsInteger(String key) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch(Exception e) {
			return 0;
		}
	}
	
	/**
	 * This method returns the value according to the key
	 * as boolean
	 * @param key
	 * @return
	 */
	public boolean getPropertyAsBoolean(String key) {
		try {
			return Boolean.parseBoolean(getProperty(key));
		} catch(Exception e) {
			return false;
		}
	}
}
