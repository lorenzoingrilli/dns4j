package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class DNSClient implements SyncResolver {

    public static final int DEFAULT_PORT = 53;
    public static final int DEFAULT_TIMEOUT = 2500;
    public static final int DEFAULT_NUM_ATTEMPTS = 3;

    private Serializer serializer = new SerializerImpl();
    
    private int timeout = DEFAULT_TIMEOUT;
    private int numAttempts = DEFAULT_NUM_ATTEMPTS;
    private boolean udpEnabled = true;
    private boolean tcpEnabled = true;
    private List<InetSocketAddress> servers = new LinkedList<InetSocketAddress>();

	public DNSClient() {
    }

    @Override
    public synchronized Message query(Message request) {
		try {
			Message resp = null;
			if(udpEnabled)
				resp = udpQueryAll(request, numAttempts, timeout);
			if( (resp==null || resp.getHeader().isTruncated()) && tcpEnabled) {
	            //resp = tcpQuery(host, port, request);
	        }
			return resp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    private Message udpQueryAll(Message request, int numTries, int timeout) throws IOException {    	
    	for(SocketAddress sa: servers) {
    		Message resp = udpQuery(sa, request, numTries, timeout);
    		if(resp!=null)
    			return resp;
    	}
    	return null;
    }
    
    private Message udpQuery(SocketAddress socketAddress, Message request, int numTries, int timeout) throws IOException {
        byte[] buffer = new byte[512];
        int len = serializer.serialize(request, buffer);
    	Message resp = null;
    	int triesCount = 0;
    	boolean success = false;    	
        DatagramSocket socket = UDP.open(timeout);
        while(!success && triesCount<numTries) 
	        try {
	        	triesCount++;
	        	UDP.send(socket, socketAddress, buffer, len);
	            /*DatagramPacket udpResp =*/ UDP.receive(socket, buffer);
	            //TODO check: src host/port should be equals to request host/port
	            resp = serializer.deserialize(buffer);            
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
    
    private Message tcpQuery(InetAddress host, int port, Message request) throws IOException {
    	return null;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) throws SocketException {
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

}

