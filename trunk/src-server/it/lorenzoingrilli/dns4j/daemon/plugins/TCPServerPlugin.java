/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.plugins;

import it.lorenzoingrilli.dns4j.daemon.EventRecv;
import it.lorenzoingrilli.dns4j.daemon.EventSent;
import it.lorenzoingrilli.dns4j.daemon.Plugin;
import it.lorenzoingrilli.dns4j.daemon.Kernel;
import it.lorenzoingrilli.dns4j.daemon.resolver.ServerQueryContext;
import it.lorenzoingrilli.dns4j.daemon.resolver.ServerSyncResolver;
import it.lorenzoingrilli.dns4j.net.TCP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

/**
 * TCP Server Plugin
 * 
 * Handle a tcp port for serve tcp dns requests
 *  
 * @author Lorenzo Ingrilli
 *
 */
public class TCPServerPlugin implements Runnable, Plugin {

	private Kernel kernel;
	
	private ServerSocket ssocket = null;
	private ServerSyncResolver<ServerQueryContext> resolver;
	private Executor executor;
	private Serializer serializer;

	private int port = TCP.DEFAULT_PORT;
	private int recvBufferSize = TCP.DEFAULT_RECV_BUFFER_SIZE;
	private int timeout = TCP.DEFAULT_TIMEOUT;	
	private int backlog = TCP.DEFAULT_BACKLOG;
	private InetAddress bindAddress = TCP.DEFAULT_BINDADDRESS;
	
	@ConstructorProperties(value={"port", "resolver", "executor", "serializer"})
	public TCPServerPlugin(int port, ServerSyncResolver<ServerQueryContext> resolver, Executor executor, Serializer serializer) {
		this.resolver = resolver;
		this.executor = executor;
		this.serializer = serializer;
		this.port = port;
	}
	
	@Override
	public void init(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void run() {
		try {
			ssocket = TCP.server(port, backlog, bindAddress, timeout, recvBufferSize);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
				
		while(!Thread.interrupted())
		try
		{
			final Socket socket = ssocket.accept();			
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
					long ts = System.currentTimeMillis();
					// WARN: we should manage multiple request on the same tcp connection
					Message request = serializer.deserialize(socket.getInputStream());
					kernel.signal(new EventRecv(this, request, ts));
					Message response = resolver.query(request, new ServerQueryContext(socket.getInetAddress(), socket.getPort()));
					if(response==null) return;
					serializer.serialize(response, socket.getOutputStream());
					ts = System.currentTimeMillis();
					kernel.signal(new EventSent(this, response, ts));
					//socket.close();
					}
					catch(IOException e) {
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
		
		try {
			TCP.close(ssocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ServerSyncResolver<ServerQueryContext> getResolver() {
		return resolver;
	}

	public void setResolver(ServerSyncResolver<ServerQueryContext> resolver) {
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
	
	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public int getRecvBufferSize() {
		return recvBufferSize;
	}

	public void setRecvBufferSize(int recvBufferSize) {
		this.recvBufferSize = recvBufferSize;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

}
