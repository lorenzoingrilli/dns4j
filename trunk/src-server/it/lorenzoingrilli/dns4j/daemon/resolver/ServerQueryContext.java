/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.resolver;

import java.net.InetAddress;

/**
 * @author Lorenzo Ingrilli'
 */
public class ServerQueryContext {
	private InetAddress address;
	private int port;
	
	public ServerQueryContext(InetAddress address, int port) {
		super();
		this.address = address;
		this.port = port;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}

}
