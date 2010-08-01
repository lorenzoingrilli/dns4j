package it.lorenzoingrilli.dns4j.daemon.resolver;

import java.net.InetAddress;

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
