package org.dcm.services.cli;

import java.util.Arrays;
import java.util.List;

import org.dcm.services.jetty.JettyServer;

import com.inodes.util.CollectionFactory;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class StartServer extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "port", "Server Port" ).withRequiredArg().ofType( Integer.class );
		parser.accepts( "jmx", "JMX Port" ).withRequiredArg().ofType( Integer.class );
		parser.accepts( "context", "Contexts to load" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void main(String args[]) throws Exception {

		// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
		OptionSet options = parser.parse(args);

		Integer connectionPort = 8080;
		Integer jmxPort = null;
		List<String> contexts = CollectionFactory.createList();
		
		try {
			if( options.has("port")) connectionPort = (Integer)options.valueOf("port");
			if( options.has("jmx")) jmxPort = (Integer)options.valueOf("jmx");
			if( options.has("context")) {
				contexts.addAll(Arrays.asList(((String)options.valueOf("context")).split(",")));
			}
		} catch( Exception e ) {
			e.printStackTrace();
			usage(parser);
		}
		
		JettyServer.startup(connectionPort, jmxPort, contexts);

	}

}
