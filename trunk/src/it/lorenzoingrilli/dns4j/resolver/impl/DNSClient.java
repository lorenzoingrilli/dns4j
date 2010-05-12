package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.impl.DeserializatorImpl;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializatorImpl;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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

    public DNSClient(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Message query(Message request) {
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
            send(socket, buffer, len);
            //TODO check: src host/port should be equals to request host/port
            while(!success) {
                DatagramPacket udpResp = receive(socket, buffer);
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
    }

    private void send(DatagramSocket socket, byte[] request, int requestLen) throws IOException {
        DatagramPacket packet = new DatagramPacket(request, requestLen, host, port);
        socket.send(packet);
    }

    private DatagramPacket receive(DatagramSocket socket, byte[] response) throws IOException {
        DatagramPacket packet = new DatagramPacket(response, response.length);
        socket.receive(packet);
        return packet;
    }

    private int receive(InputStream is, byte[] response) throws IOException {
	 int letti = 0;
         int r = 0;
	 while(r>=0) {
            r = is.read(response, letti, response.length-letti);
            letti += r;
	 }
         return letti;
    }

    private void send(OutputStream os, byte[] request, int len) throws IOException {
        os.write(request, 0, len);
    }
    
    public int tcpQuery(byte[] request, int requestLen, byte[] response) throws IOException {
    	Socket socket = new Socket(host, port);
    	OutputStream os = socket.getOutputStream(); 
    	InputStream is = socket.getInputStream();
        send(os, request, requestLen);
        int letti = receive(is, response);
        is.close();
        os.close();
    	socket.close();
        return letti;
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

