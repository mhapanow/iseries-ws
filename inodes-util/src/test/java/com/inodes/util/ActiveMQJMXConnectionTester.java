package com.inodes.util;

import java.util.Iterator;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Test;

import com.inodes.util.activemq.ActiveMQJMXConnection;

import junit.framework.TestCase;

public class ActiveMQJMXConnectionTester extends TestCase {

	@Test
	public void testConnection() {
		try {
			System.out.println("Connecting...");
			ActiveMQJMXConnection conn = new ActiveMQJMXConnection().connect();
			
			System.out.println("Adding queue new.destination...");
			conn.getBroker().addQueue("new.destination");
			
			Iterator<String> i = conn.getQueues().keySet().iterator();
			while(i.hasNext()) {
				QueueViewMBean q = conn.getQueue(i.next());
				System.out.println("Listing queue " + q.toString());
			}

			i = conn.getQueues().keySet().iterator();
			while(i.hasNext()) {
				String qn = i.next();
				System.out.println("Deleting queue " + qn + "...");
				conn.getBroker().removeQueue(qn);
			}

			System.out.println("Disconnecting...");
			conn.disconnect();
			
		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}
}
