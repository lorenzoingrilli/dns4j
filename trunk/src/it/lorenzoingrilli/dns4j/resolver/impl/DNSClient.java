package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class DNSClient implements SyncResolver {

    public static final int DEFAULT_PORT = 53;
    public static final int DEFAULT_TIMEOUT = 5000;
    public static final int DEFAULT_NUM_ATTEMPTS = 3;

    private InetAddress host;
    private int port;
    private int timeout = DEFAULT_TIMEOUT;
    private int numAttempts = DEFAULT_NUM_ATTEMPTS;
    private Serializer serializer = new SerializerImpl();

    public DNSClient(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    /*
    @Override
    public synchronized Message query(Message request) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
        byte[] buffer = new byte[512];
        int len = SerializatorImpl.serialize(request, buffer);
        Message resp = null;
        int retryCount = 0;
        boolean success = false;
    	while(!success)
        try {
            UDP.send(socket, host, port, buffer, len);
            //TODO check: src host/port should be equals to request host/port
            while(!success) {
                DatagramPacket udpResp = UDP.receive(socket, buffer);
                resp = DeserializatorImpl.deserialize(buffer);
                if(resp.getHeader().getId()==request.getHeader().getId())
                    success = true;
                else
                    resp = null;
            }
            success = true;
    	}
    	catch(IOException e) {
    		if(e instanceof SocketTimeoutException) {
    			retryCount++;
    			if(retryCount>=numAttempts) {
    				socket.close();
    				throw new RuntimeException(e);
    			}
    		}
    		else {
    			socket.close();
    			throw new RuntimeException(e);
    		}
    	}
        socket.close();
        if(resp.getHeader().isTruncated()) {
            // TODO: TCP call
        }
        return resp;
    }*/
    
    @Override
    public synchronized Message query(Message request) {
		try {
			Message resp = query(host, port, request, numAttempts, timeout);
			if(resp.getHeader().isTruncated()) {
	            // TODO: TCP call
	        }
			return resp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    private Message query(InetAddress host, int port, Message request, int numTries, int timeout) throws IOException {
        byte[] buffer = new byte[512];
        int len = serializer.serialize(request, buffer);
    	Message resp = null;
    	int triesCount = 0;
    	boolean success = false;    	
        DatagramSocket socket = UDP.open(timeout);
        while(!success && triesCount<numTries) 
	        try {
	        	triesCount++;
	        	UDP.send(socket, host, port, buffer, len);
	            DatagramPacket udpResp = UDP.receive(socket, buffer);
	            //TODO check: src host/port should be equals to request host/port
	            resp = serializer.deserialize(buffer);            
	            if(resp.getHeader().getId()==request.getHeader().getId())
	                success = true;
	            else
	                resp = null;
	        }
	        catch(SocketTimeoutException e) {
	        }
        UDP.close(socket);
        return resp;
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

}

