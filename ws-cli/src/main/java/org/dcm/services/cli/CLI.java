package org.dcm.services.cli;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.inodes.util.FileLoader;

import joptsimple.OptionParser;
import joptsimple.OptionSet;


public class CLI {

	private static final Logger logger = Logger.getLogger(CLI.class);

	static {
		try {
			try {
				URL url = FileLoader.getResource("log4j.properties", FileLoader.PRECEDENCE_SYSTEMPATH);
				PropertyConfigurator.configure(url);
			} catch( Exception e ) {}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		System.setProperty("log4j.configurationFile", FileLoader.getResource("log4j2.xml", FileLoader.PRECEDENCE_SYSTEMPATH).getFile());

		if (System.getProperty("dynamic.loader") == null ) {
			System.setProperty("dynamic.loader", "lib");
		}

		String className;
		if(!args[0].contains("\\.")) {
			className = "org.dcm.services.cli." + args[0];
		} else {
			className = args[0];
		}

		List<String> newArgList = new ArrayList<String>();
		for( int i = 1; i < args.length; i++ ) {
			newArgList.add(args[i]);
		}

		OptionParser parser = new OptionParser();
		parser.accepts( "appcontext", "Application Context File. Defauts to $ALLSHOPPINGS/etc/baseApplicationContext.xml" ).withRequiredArg().ofType( String.class );
		parser.accepts( "nocontext", "Don't use an application context. This is used for StartServer" );
		parser.accepts( "help", "Shows this help message" );

		Class<?> clazz = (Class<?>)Class.forName(className);

		Method buildOptionParser = clazz.getDeclaredMethod("buildOptionParser", OptionParser.class);
		buildOptionParser.invoke(null, new Object[] {parser});

		OptionSet options = parser.parse(args);

		String appcontext = null;
		if( options.has("appcontext")) {
			appcontext = options.valueOf("appcontext").toString();
		}

		final ConfigurableApplicationContext context = options.has("nocontext") ? null :
			new FileSystemXmlApplicationContext( appcontext == null 
			? "/" + FileLoader.getResource("baseApplicationContext.xml",
					FileLoader.PRECEDENCE_SYSTEMPATH).getFile()
					: "/" + appcontext );


		try {
			Method setApplicationContext = clazz.getDeclaredMethod("setApplicationContext", ApplicationContext.class);
			setApplicationContext.invoke(null, new Object[] {context});
		} catch( Throwable t ) {}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.debug("Shutting down - closing application");
				// Shut down everything (e.g. threads) that you need to.

				// then shut down log4j
				if( LogManager.getContext() instanceof LoggerContext ) {
					logger.debug("Shutting down log4j2");
					Configurator.shutdown((LoggerContext)LogManager.getContext());
				} else
					logger.warn("Unable to shutdown log4j2");

				if( context != null )
					context.close();
			}
		});

		if( options.has("help")) {
			try {
				AbstractCLI.usage(parser);
			} catch( Exception e ) {}
		} else {
			try {
				Method m = clazz.getDeclaredMethod("main", String[].class);
				m.invoke(null, new Object[] {newArgList.toArray(new String[newArgList.size()])});
			} catch( Throwable t ) {
				t.printStackTrace();
			}
		}

		System.exit(0);
	}
}
