package org.dcm.services.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.dcm.services.exception.DCMException;
import org.dcm.services.impl.ConnectionHelperImpl;
import org.dcm.services.impl.WSBrokerImpl;
import org.dcm.services.model.SystemConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@SuppressWarnings("serial")
public class InitAppServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(InitAppServlet.class.getName());
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		try {
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
			final SystemConfiguration systemConfiguration = (SystemConfiguration)context.getBean("system.configuration");
			
			// Starts all consumers
			for( int i = 0; i < systemConfiguration.getConsumers(); i++ ) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							ConnectionHelperImpl conn = new ConnectionHelperImpl(systemConfiguration);
							WSBrokerImpl broker = new WSBrokerImpl(conn, systemConfiguration);
							broker.startConsumer();
						} catch( DCMException e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				});
				t.setName("WS Consumer " + i);
				t.start();
			}
			
		} catch( Throwable t ) {
		} finally {
			log.log(Level.INFO, "Basic data initialized!");
		}
	}

}
