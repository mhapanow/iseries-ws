package org.dcm.services.cli;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.impl.WSBrokerImpl;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;

public class TestConsumer extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TestConsumer.class.getName());

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
			WSBrokerImpl broker = (WSBrokerImpl)getApplicationContext().getBean("ws.broker");

			log.log(Level.INFO, "Starting consumer....");
			broker.startConsumer();

		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);

	}

}
