package it.lorenzoingrilli.dns4j.daemon.plugins;

import it.lorenzoingrilli.dns4j.daemon.EventDispatcher;
import it.lorenzoingrilli.dns4j.daemon.EventRecv;
import it.lorenzoingrilli.dns4j.daemon.EventSent;
import it.lorenzoingrilli.dns4j.daemon.Plugin;
import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.impl.Serialization;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

public class UDPServerPlugin implements Runnable, Plugin {

	private int timeout = UDP.DEFAULT_TIMEOUT;
	private DatagramSocket socket = null;
	private SyncResolver resolver;
	private Executor executor;
	private int port;
	private EventDispatcher dispatcher;
	
	public UDPServerPlugin() { }
	
	public UDPServerPlugin(int port, SyncResolver resolver, Executor executor) {
		this.resolver = resolver;
		this.executor = executor;
		this.port = port;
	}
	
	@Override
	public void init(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void run() {
		try {
			socket = UDP.open(port, timeout);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		
		while(!Thread.interrupted())
		try
		{
			final byte buffer[] = new byte[UDP.DEFAULT_BUFFER_SIZE];
			final DatagramPacket packet = UDP.receive(socket, buffer);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Message request = Serialization.deserialize(packet.getData());
					dispatcher.dispatch(new EventRecv(this, request));
					Message response = resolver.query(request);
					dispatcher.dispatch(new EventSent(this, response));
					int len = Serialization.serialize(response, buffer);
					try {
						UDP.send(socket, packet.getAddress(), packet.getPort(), buffer, len);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		catch(Exception e)
		{
			if(! (e instanceof SocketTimeoutException))
				e.printStackTrace();
		}
		
		UDP.close(socket);		
	}
	
	public SyncResolver getResolver() {
		return resolver;
	}

	public void setResolver(SyncResolver resolver) {
		this.resolver = resolver;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
