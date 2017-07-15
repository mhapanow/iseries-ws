package org.dcm.services.cli;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.impl.ConnectionHelperImpl;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;

public class TestConnection extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TestConnection.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}


	public static void main(String args[]) throws Exception {

		try {
			ConnectionHelperImpl conn = (ConnectionHelperImpl)getApplicationContext().getBean("conn.helper");

			log.log(Level.INFO, "Testing connection....");
			conn.connect();
			log.log(Level.INFO, "Connection established: " + conn.isConnected());
			conn.disconnect();

		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);

	}

}
