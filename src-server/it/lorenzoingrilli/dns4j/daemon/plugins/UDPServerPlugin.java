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
import it.lorenzoingrilli.dns4j.daemon.TPExecutor;
import it.lorenzoingrilli.dns4j.daemon.resolver.ServerQueryContext;
import it.lorenzoingrilli.dns4j.daemon.resolver.ServerSyncResolver;
import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.resolver.DnsEventListener;
import it.lorenzoingrilli.dns4j.resolver.AsyncResolver;
import it.lorenzoingrilli.dns4j.resolver.Resolver;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

/**
 * UDP Server Plugin
 * 
 * Handle a tcp port for serve tcp dns requests
 *  
 * @author Lorenzo Ingrilli
 *
 */
public class UDPServerPlugin implements Runnable, Plugin {
	
	private int timeout = UDP.DEFAULT_TIMEOUT;
	private DatagramSocket socket = null;
	private Resolver resolver;
	private Executor executor;
	private Serializer serializer;
	private int port;
	private int sendBufferSize = UDP.DEFAULT_SEND_BUFFER_SIZE;
	private int recvBufferSize = UDP.DEFAULT_RECV_BUFFER_SIZE;
	private int maxPacketSize = UDP.MAX_PACKET_SIZE;
	private InetAddress bindAddress = UDP.DEFAULT_BINDADDRESS;
	
	private Kernel kernel;
	
	@ConstructorProperties(value={"port", "resolver", "executor", "serializer"})
	public UDPServerPlugin(int port, Resolver resolver, Executor executor, Serializer serializer) {
		this.resolver = resolver;
		this.executor = executor!=null?executor:TPExecutor.getDefault();
		this.serializer = serializer!=null?serializer:new SerializerImpl();
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
			socket = UDP.open(port, timeout, bindAddress, sendBufferSize, recvBufferSize);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		
		while(!Thread.currentThread().isInterrupted())
		try
		{
			final byte buffer[] = new byte[maxPacketSize];
			final DatagramPacket packet = UDP.receive(socket, buffer);
			final long ts = System.currentTimeMillis();
			
			// skip packets greater than 512 bytes
			if(packet.getLength()>maxPacketSize) continue;
			
			executor.execute(new Runnable() {
				@Override
				public void run() {										
					Message request = serializer.deserialize(packet.getData(), packet.getOffset(), packet.getLength());
					kernel.signal(new EventRecv(this, request, ts));
					
					if(resolver instanceof AsyncResolver) {
						((AsyncResolver) resolver).asyncQuery(request, new SendResponse(packet.getAddress(), packet.getPort(), serializer, socket));
						return;						
					}
					
					Message response = ((ServerSyncResolver<ServerQueryContext>) resolver).query(request, new ServerQueryContext(packet.getAddress(), packet.getPort()));
					if(response==null) return;
					int len = serializer.serialize(response, buffer);
					try {
						UDP.send(socket, packet.getAddress(), packet.getPort(), buffer, len);
					} catch (IOException e) {
						e.printStackTrace();
					}
					long ts2 = System.currentTimeMillis();
					kernel.signal(new EventSent(this, response, ts2));
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
	
	public Resolver getResolver() {
		return resolver;
	}

	public void setResolver(Resolver resolver) {
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

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
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

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

}

class SendResponse implements DnsEventListener {

	private InetAddress address;
	private int port;
	private Serializer serializer;
	private DatagramSocket socket;
	
	public SendResponse(InetAddress address, int port, Serializer serializer, DatagramSocket socket) {
		super();
		this.address = address;
		this.port = port;
		this.serializer = serializer;
		this.socket = socket;
	}

	@Override
	public void onResponse(Message request, Message response) {
		final byte buffer[] = new byte[UDP.DEFAULT_BUFFER_SIZE];
		int len = serializer.serialize(response, buffer);
		try {
			UDP.send(socket, address, port, buffer, len);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onException(byte[] message, Exception e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRequest(Message request) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTimeout(Message request) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUnexpectedResponse(Message response) {
		// TODO Auto-generated method stub
		
	}
	
}