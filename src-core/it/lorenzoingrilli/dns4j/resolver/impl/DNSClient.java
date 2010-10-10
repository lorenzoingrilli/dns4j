/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.A;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;
import it.lorenzoingrilli.dns4j.resolver.DnsEventListener;
import it.lorenzoingrilli.dns4j.resolver.NetEventListener;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lorenzo Ingrilli'
 */
public class DNSClient implements SyncResolver {

	public static final String DEFAULT_SERVER = "localhost";
    public static final Integer DEFAULT_PORT = 53;
    public static final Integer DEFAULT_TIMEOUT = 2500;
    public static final Integer DEFAULT_NUM_ATTEMPTS = 3;
    public static final Integer DEFAULT_NDOTS = 1;

    private Serializer serializer = new SerializerImpl();
    
    private String domain = null;
    private int timeout = DEFAULT_TIMEOUT;
    private int numAttempts = DEFAULT_NUM_ATTEMPTS;
    private int ndots = DEFAULT_NDOTS;
    private boolean udpEnabled = true;
    private boolean tcpEnabled = true;    
    private List<InetSocketAddress> servers = new LinkedList<InetSocketAddress>();
    private InetSocketAddress current;
    
    private NetEventListener netEventListener;
    private DnsEventListener dnsEventListener;

	public DNSClient() {
    }
    
	public InetAddress getHostByName(String name) throws UnknownHostException {
		Message req =
			new MessageBuilder()
			.question()
			.recursionDesidered()
			.addQuestion(name, Type.A, Clazz.IN)
			.message();

		Message resp = query(req);
		if(resp==null)
			throw new UnknownHostException();
		
		for(RR rr: resp.getAnswer()) {
			if(rr instanceof A)
				return ((A) rr).getAddress();
		}
		
		throw new UnknownHostException();
	}
	
    @Override
    public synchronized Message query(Message request) {
		try {
			Message resp = null;
			if(udpEnabled)
				resp = udpQueryAll(request, numAttempts, timeout);
			if( (!udpEnabled || (resp!=null && resp.getHeader().isTruncated())) && tcpEnabled) {
	            resp = tcpQuery(current.getAddress(), current.getPort(), request);
	        }
			return resp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    private Message udpQueryAll(Message request, int numTries, int timeout) throws IOException {    	
    	for(InetSocketAddress sa: servers) {
    		Message resp = udpQuery(sa, request, numTries, timeout);
    		if(resp!=null) {
    			current = sa;
    			return resp;
    		}
    	}
    	return null;
    }
    
    private Message udpQuery(SocketAddress socketAddress, Message request, int numTries, int timeout) throws IOException {
        byte[] buffer = new byte[512];
        int len = serializer.serialize(request, buffer);
    	Message resp = null;
    	int triesCount = 0;
    	boolean success = false;    	
        DatagramSocket socket = UDP.open(timeout, UDP.DEFAULT_SEND_BUFFER_SIZE, UDP.DEFAULT_RECV_BUFFER_SIZE);
        while(!success && triesCount<numTries) 
	        try {
	        	triesCount++;
	        	DatagramPacket udpReq = UDP.send(socket, socketAddress, buffer, len);
	            if(netEventListener!=null) {
	            	netEventListener.onSent(udpReq.getData(), udpReq.getOffset(), udpReq.getLength());
	            }
	            DatagramPacket udpResp = UDP.receive(socket, buffer);
	            if(netEventListener!=null) {
	            	netEventListener.onRecv(udpResp.getData(), udpResp.getOffset(), udpResp.getLength());
	            }
	            //TODO check: src host/port should be equals to request host/port
	            resp = serializer.deserialize(udpResp.getData(), udpResp.getOffset(), udpResp.getLength());            
	            if(resp.getHeader().getId()==request.getHeader().getId())
	                success = true;
	            else
	                resp = null;
	        }
	        catch(SocketTimeoutException e) {
	        	e.printStackTrace();
	        }
        UDP.close(socket);
        return resp;
    }
    
    // TODO implement
    private Message tcpQuery(InetAddress host, int port, Message request) throws IOException {
    	Socket s = new Socket(host, port);
    	try {
	    	OutputStream os = s.getOutputStream();
	    	InputStream is = s.getInputStream();
	    	serializer.serialize(request, os);
	    	Message response =  serializer.deserialize(is);
	    	return response;
    	}
    	finally {
    		s.close();
    	}    	
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getNumAttempts() {
	return numAttempts;
    }

    public void setNumAttempts(int numAttempts) {
        this.numAttempts = numAttempts;
    }
    
    public boolean isUdpEnabled() {
		return udpEnabled;
	}

	public void setUdpEnabled(boolean udpEnabled) {
		this.udpEnabled = udpEnabled;
	}

	public boolean isTcpEnabled() {
		return tcpEnabled;
	}

	public void setTcpEnabled(boolean tcpEnabled) {
		this.tcpEnabled = tcpEnabled;
	}
	
	public void addServer(InetAddress address, int port) {
		servers.add(new InetSocketAddress(address, port));
	}
	
	public void addServer(InetSocketAddress socketAddress) {
		servers.add(socketAddress);
	}
	
	public List<InetSocketAddress> getServers() {
		return servers;
	}

	public void setServers(List<InetSocketAddress> servers) {
		this.servers = servers;
	}

	public NetEventListener getNetEventListener() {
		return netEventListener;
	}

	public void setNetEventListener(NetEventListener netEventListener) {
		this.netEventListener = netEventListener;
	}

	public DnsEventListener getDnsEventListener() {
		return dnsEventListener;
	}

	public void setDnsEventListener(DnsEventListener dnsEventListener) {
		this.dnsEventListener = dnsEventListener;
	}

	public int getNdots() {
		return ndots;
	}

	public void setNdots(int ndots) {
		this.ndots = ndots;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}

