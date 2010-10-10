/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.util;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

public class JmxServer {

	private Logger logger = Logger.getLogger(JmxServer.class.getName());

	public static final int DEFAULT_PORT = 8999;		
	public static final String DEFAULT_ADDRESS = "localhost";

	private Registry registry = null;
	private int port = DEFAULT_PORT;
	private String address = DEFAULT_ADDRESS;
	private JMXAuthenticator authenticator;
	private JMXConnectorServer cs = null;
	private MBeanServer mbs = null;
	private boolean started = false;

	public JmxServer(MBeanServer mbs, JMXAuthenticator auth)
	{
		this.mbs = mbs;
		this.authenticator = auth;
	}

	public void start() throws IOException
	{
		if(!started)
		{					
			registry = LocateRegistry.createRegistry(port);		
			JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+address+":"+port+"/jmxrmi");				 
			HashMap<String, JMXAuthenticator> env = new HashMap<String, JMXAuthenticator>();
			env.put(JMXConnectorServer.AUTHENTICATOR, authenticator);
			cs = JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, env, mbs);
			cs.start();					
			started = true;
		}
	}

	public void stop() throws AccessException, RemoteException
	{
		if(started)
		{
			started = false;
			try {
				cs.stop();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Non sono riusito ad arrestare il connettore JMX", e);
			}

			if(registry!=null) {
				UnicastRemoteObject.unexportObject(registry, true);
			}
		}
	}

	public boolean isStarted()
	{
		return started;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int rmiPort) {
		this.port = rmiPort;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}