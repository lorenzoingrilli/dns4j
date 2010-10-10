/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.util;

import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;

public class JmxAuthenticator implements JMXAuthenticator {

	private static Logger logger = Logger.getLogger(JmxAuthenticator.class.getName());

	private String username;
	private String password;

	public JmxAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public Subject authenticate(Object credentials) {

		if (credentials == null) {
			throw new SecurityException("Credentials required");
		}

		if (!(credentials instanceof String[])) {
			throw new SecurityException("Credentials should be String[]");
		}

		String[] creds = (String[]) credentials;			
		if (creds.length != 2) {
			throw new SecurityException("Credentials should have 2 elements (username, password)");
		}

		String username = creds[0];
		String password = creds[1];
		String hostname = null;

		try {	
			hostname = RemoteServer.getClientHost();	
		} catch (ServerNotActiveException e) {
			throw new SecurityException("Server not active");
		} 

		if (!(this.username.equals(username) && this.password.equals(password))) {
			logger.log(Level.SEVERE, "LOGIN FAILED username="+username+", ip="+hostname);
			throw new SecurityException("Access Denied");
		}   

		logger.log(Level.INFO, "LOGIN username="+username+", ip="+hostname);

		return new Subject(true,
				Collections.singleton(new JMXPrincipal(username)),
				Collections.EMPTY_SET,
				Collections.EMPTY_SET);		
	}

}
