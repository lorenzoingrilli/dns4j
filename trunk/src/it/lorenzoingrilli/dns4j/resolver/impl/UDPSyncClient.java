package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.impl.DeserializatorImpl;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializatorImpl;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class UDPSyncClient implements SyncResolver {

    public static final int DEFAULT_PORT = 53;
    public static final int DEFAULT_TIMEOUT = 250;
    public static final int DEFAULT_NUM_ATTEMPTS = 5;

    private InetAddress host;
    private int port;
    private DatagramSocket socket;

    public UDPSyncClient(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    public void open() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(DEFAULT_TIMEOUT);
    }

    public void close() {
        socket.close();
    }

    @Override
    public Message query(Message request) {
    	try {
	        //TODO check: response ID have to be equals to request ID
	        byte[] buffer = new byte[512];
	        int len = SerializatorImpl.serialize(request, buffer);
	        query(buffer, len, buffer);
	        Message resp = DeserializatorImpl.deserialize(buffer);
	        return resp;
    	}
    	catch(IOException e) {
    		throw new RuntimeException(e);
    	}
    }

    public int query(byte[] request, int requestLen, byte[] response) throws IOException { 
        DatagramPacket packet = new DatagramPacket(request, requestLen, host, port);
        socket.send(packet);
        DatagramPacket pResp = new DatagramPacket(response, response.length);
        socket.receive(pResp);
    	//TODO check: src port have to be equals to request port
        return pResp.getLength();
    }

}
