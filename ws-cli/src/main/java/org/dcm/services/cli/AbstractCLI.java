package org.dcm.services.cli;

import java.io.IOException;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;

public abstract class AbstractCLI {

	static OptionParser parser;
	static ApplicationContext context;
	
	public static void usage(OptionParser parser) throws IOException {
		parser.printHelpOn(System.out);
		System.exit(-1);
	}
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}
	
	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static ApplicationContext getApplicationContext() {
		return context;
	}

}
