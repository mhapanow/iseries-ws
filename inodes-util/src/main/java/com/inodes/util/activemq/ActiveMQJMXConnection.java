package com.inodes.util.activemq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

public class ActiveMQJMXConnection {

	public static final String DEFAULT_BROKER = "localhost";
	public static final int DEFAULT_PORT = 1616;
	
	private String jmxUrl;
	private String brokerName;
	private MBeanServerConnection conn;
	private HashMap<String, QueueViewMBean> queues;
	private BrokerViewMBean broker;
	private JMXConnector jmxc;
	
	public ActiveMQJMXConnection() {
		this(DEFAULT_BROKER, DEFAULT_PORT);
	}

	public ActiveMQJMXConnection(String jmxUrl, String brokerName) {
		this.brokerName = brokerName;
		this.jmxUrl = jmxUrl;
		queues = new HashMap<String, QueueViewMBean>();
	}

	public ActiveMQJMXConnection(String brokerName, int port) {
		this("service:jmx:rmi:///jndi/rmi://" + brokerName + ":" + port + "/jmxrmi", "localhost");
	}
	
	public ActiveMQJMXConnection connect() throws IOException, MalformedObjectNameException, NullPointerException {
		return connect(jmxUrl, brokerName);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ActiveMQJMXConnection connect(String jmxUrl, String brokerName) throws IOException, MalformedObjectNameException, NullPointerException {
        Map m =  new  HashMap ();  
        m.put ( "jmx.remote.x.client.connection.check.period" , 0L);  
		this.jmxUrl = jmxUrl;
		this.brokerName = brokerName;
		JMXServiceURL url;
		url = new JMXServiceURL(jmxUrl);
		jmxc = JMXConnectorFactory.connect(url, m);
		conn = jmxc.getMBeanServerConnection();
		loadQueues();
		return this;
	}

	public void disconnect() throws IOException {
		jmxc.close();
	}
	
	public void loadQueues() throws MalformedObjectNameException, NullPointerException {
		ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=" + brokerName + ",Type=Broker");
		broker = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ,BrokerViewMBean.class, true);

		for (ObjectName name : broker.getQueues()) {
			QueueViewMBean queueMbean = (QueueViewMBean)
			MBeanServerInvocationHandler.newProxyInstance(conn, name, QueueViewMBean.class, true);

			queues.put(queueMbean.getName(), queueMbean);
		} 
	}

	public QueueViewMBean getQueue(String key) throws MalformedObjectNameException, NullPointerException {
		loadQueues();
		return queues.get(key);
	}
	
	/**
	 * @return the jmxUrl
	 */
	public String getJmxUrl() {
		return jmxUrl;
	}

	/**
	 * @param jmxUrl the jmxUrl to set
	 */
	public void setJmxUrl(String jmxUrl) {
		this.jmxUrl = jmxUrl;
	}

	/**
	 * @return the conn
	 */
	public MBeanServerConnection getConn() {
		return conn;
	}

	/**
	 * @return the brokerName
	 */
	public String getBrokerName() {
		return brokerName;
	}

	/**
	 * @param brokerName the brokerName to set
	 */
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	/**
	 * @return the queues
	 * @throws NullPointerException 
	 * @throws MalformedObjectNameException 
	 */
	public HashMap<String, QueueViewMBean> getQueues() throws MalformedObjectNameException, NullPointerException {
		loadQueues();
		return queues;
	}

	/**
	 * @return the broker
	 */
	public BrokerViewMBean getBroker() {
		return broker;
	}
}
