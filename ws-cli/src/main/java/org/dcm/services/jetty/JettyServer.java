package org.dcm.services.jetty;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.remote.JMXServiceURL;

import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import com.inodes.util.FileLoader;

public class JettyServer {

	private final static Integer DEFAULT_PORT = 8080; 

	public static void startup(Integer connectorPort, Integer jmxPort, List<String> contexts) throws Exception {

		// Defines server and port
		Server server;
		if( connectorPort != null && connectorPort > 0 ) {
			server = new Server(connectorPort);
		} else {
			server = new Server(DEFAULT_PORT);
		}

		if( jmxPort != null && jmxPort > 0 ) {
			// Setup JMX
			MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
			server.addBean(mbContainer);
			ConnectorServer jmxConnector = new ConnectorServer(new JMXServiceURL("rmi", null, jmxPort, "/jndi/rmi://localhost:" + jmxPort + "/jmxrmi"), "org.eclipse.jetty.jmx:name=rmiconnectorserver");
			jmxConnector.start();
		}

		if( contexts == null || contexts.size() == 0 || contexts.contains("ws")) {

			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath("/ws");
			File warFile = new File(FileLoader.getResource("ws-services-0.1.war").getFile());
			webapp.setWar(warFile.getAbsolutePath());

			ContextHandlerCollection multi = new ContextHandlerCollection();
			multi.setHandlers(new Handler[] { webapp });

			server.setHandler(multi);
		}
		// Starts the server up
		server.start();
		server.join();
	}
}
