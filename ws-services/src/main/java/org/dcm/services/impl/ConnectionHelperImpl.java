package org.dcm.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.dcm.services.exception.DCMException;
import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.model.SystemConfiguration;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.KeyedDataQueue;
import com.inodes.util.FileLoader;

public class ConnectionHelperImpl {

	@Autowired
	private SystemConfiguration systemConfiguration;

	private AS400 connection = new AS400();
	private DataQueue inputDtaq = null;
    private KeyedDataQueue outputDtaq = null;
	private KeyedDataQueue serverInputDtaq = null;
    private KeyedDataQueue serverOutputDtaq = null;
    private String url = null;
    private java.sql.Connection connObject = null; 
    private long connectedOn = 0L;
    
    public ConnectionHelperImpl() {
		super();
	}
    
    public ConnectionHelperImpl(SystemConfiguration systemConfiguration) {
    	this.systemConfiguration = systemConfiguration;
    }
    
	/**
	 * Connects an iSeries instance
	 * 
	 * @throws DCMException
	 */
	public void connect() throws DCMException {
		
		try {
			
            // check for connection
			if( isConnected())
				return;
			
			String password = getCredentials();
			
            // Create an AS400 object for the server that has the data queue.
			connection = new AS400(systemConfiguration.getiSeriesServer(), systemConfiguration.getiSeriesUser(), password);

			connection.setSystemName(systemConfiguration.getiSeriesServer());
			connection.setUserId(systemConfiguration.getiSeriesUser());
			connection.setPassword(password);
			
			connection.connectService(0);
			
			Class.forName("com.ibm.as400.access.AS400JDBCDriver");

			url = "jdbc:as400://" + systemConfiguration.getiSeriesServer() + ";user="
					+ systemConfiguration.getiSeriesUser() + ";password=" + password + ";";
			connObject = DriverManager.getConnection(url);
			connObject.setAutoCommit(true);

			connectedOn = System.currentTimeMillis();
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	public AS400 getAlternateConnection() throws DCMException {
		
		try {
			String password = getCredentials();

			// Create an AS400 object for the server that has the data queue.
			AS400 conn2 = new AS400(systemConfiguration.getiSeriesServer(), systemConfiguration.getiSeriesUser(), password);

			conn2.setSystemName(systemConfiguration.getiSeriesServer());
			conn2.setUserId(systemConfiguration.getiSeriesUser());
			conn2.setPassword(password);

			conn2.connectService(0);

			return conn2;
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	public String getCredentials() throws IOException {

		String credentialsfile = FileLoader.getResource("credentials", FileLoader.PRECEDENCE_SYSTEMPATH).getFile();
		File credentials = new File(credentialsfile);
		FileInputStream fis = new FileInputStream(credentials);
		byte[] b = new byte[1024];
		int len = fis.read(b);
		fis.close();
		String encrypted = new String(b, 0, len);
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("ws400");
		
		String decripted = encryptor.decrypt(encrypted);
		return decripted;
	}
	
	/**
	 * Disconnects an iSeries instance
	 * 
	 * @throws DCMException
	 */
	public void disconnect() throws DCMException {
		try {
			connection.disconnectAllServices();
			inputDtaq = null;
			outputDtaq = null;
			
			if( connObject != null ) {
				try {
					connObject.close();
				} catch( Exception e1 ) {}
				connObject = null;
			}
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the as400 connection description
	 * @return
	 */
	public AS400 getConnection() {
		return connection;
	}

	/**
	 * Checks if the instance is connected
	 * 
	 * @return True if the instance is connected, false if not
	 * @throws DCMException
	 */
	public boolean isConnected() throws DCMException {
		try {
			if( connection == null )
				return false;
			
			if( connection.isConnected() && ((System.currentTimeMillis() - connectedOn) > 300000)) {
				disconnect();
				return false;
			}
			
			return connection.isConnected();
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the WS input dtaq
	 * 
	 * @return The Web Service Input Data Queue Description
	 * @throws DCMException
	 */
	public DataQueue getInputDataQueue() throws DCMException {
		try {
			if( !isConnected())
				connect();
			
			if( inputDtaq == null )
				inputDtaq = new DataQueue(connection, "/QSYS.LIB/" + systemConfiguration.getDTAQLib() + ".LIB/" + systemConfiguration.getInDTAQName() + ".DTAQ");
			return inputDtaq;
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the WS server input dtaq
	 * 
	 * @return The Web Service Input Data Queue Description
	 * @throws DCMException
	 */
	public KeyedDataQueue getServerInputDataQueue() throws DCMException {
		try {
			if( !isConnected())
				connect();
			
			if( serverInputDtaq == null )
				serverInputDtaq = new KeyedDataQueue(connection, "/QSYS.LIB/" + systemConfiguration.getDTAQLib() + ".LIB/" + systemConfiguration.getServerInDTAQName() + ".DTAQ");
			return serverInputDtaq;
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the WS output dtaq
	 * 
	 * @return The Web Service Output Data Queue Description
	 * @throws DCMException
	 */
	public KeyedDataQueue getOutputDataQueue() throws DCMException {
		try {
			if( !isConnected())
				connect();

			if( outputDtaq == null )
				outputDtaq = new KeyedDataQueue(connection, "/QSYS.LIB/" + systemConfiguration.getDTAQLib() + ".LIB/" + systemConfiguration.getOutDTAQName() + ".DTAQ");
			return outputDtaq;
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the WS server output dtaq
	 * 
	 * @return The Web Service Output Data Queue Description
	 * @throws DCMException
	 */
	public KeyedDataQueue getServerOutputDataQueue() throws DCMException {
		try {
			if( !isConnected())
				connect();

			if( serverOutputDtaq == null )
				serverOutputDtaq = new KeyedDataQueue(connection, "/QSYS.LIB/" + systemConfiguration.getDTAQLib() + ".LIB/" + systemConfiguration.getServerOutDTAQName() + ".DTAQ");
			return serverOutputDtaq;
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the JDBC Connection
	 * 
	 * @return an established JDBC Connection
	 * @throws DCMException
	 */
	public Connection getJDBCConnection() throws DCMException {
		if( !isConnected())
			connect();
		
		return connObject;
	}

	/**
	 * Returns a JDBC Statement
	 * 
	 * @return an established JDBC Statement
	 * @throws DCMException
	 */
	public Statement getJDBCStatement() throws DCMException {
		try {
			return getJDBCConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch( SQLException e ) {
			try {
				disconnect();
				connect();
				return getJDBCConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch( Exception e1 ) {
				throw DCMExceptionHelper.defaultException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Returns the connection data library
	 * 
	 * @return The connection data library
	 */
	public String getDataLib() {
		return systemConfiguration.getDataLib();
	}

	/**
	 * Returns the service definition path
	 * 
	 * @return The service definition path
	 */
	public String getDefPath() {
		return systemConfiguration.getDefPath();
	}

	/**
	 * Returns the iSeries Charset
	 * 
	 * @return The iSeries Charset for EBCDIC/ASCII conversions
	 */
	public Charset getCharset() {
		return Charset.forName(systemConfiguration.getCharset());
	}
}
