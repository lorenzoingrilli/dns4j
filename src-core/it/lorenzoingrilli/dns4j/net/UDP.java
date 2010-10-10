/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * UDP helper methods
 *  
 * @author Lorenzo Ingrilli'
 */
public class UDP {

	public static final int DEFAULT_SEND_BUFFER_SIZE = 62464;
	public static final int DEFAULT_RECV_BUFFER_SIZE = 62464;
	public static final int DEFAULT_BUFFER_SIZE = 512;	
	public static final int DEFAULT_TIMEOUT = 5000;
	public static final int DEFAULT_PORT = 53;
	public static final int MAX_PACKET_SIZE = 512;
	public static final InetAddress DEFAULT_BINDADDRESS = null;
	
	public static DatagramSocket open(int timeout, int sendBufferSize, int recvBufferSize) throws SocketException {
		DatagramSocket socket = new DatagramSocket();
		socket.setSendBufferSize(sendBufferSize);
		socket.setReceiveBufferSize(recvBufferSize);
		socket.setSoTimeout(timeout);
		return socket;
	}
	
	public static DatagramSocket open(int port, int timeout, InetAddress bindAddress, int sendBufferSize, int recvBufferSize) throws SocketException {
		DatagramSocket socket = new DatagramSocket(port, bindAddress);
		socket.setSendBufferSize(sendBufferSize);
		socket.setReceiveBufferSize(recvBufferSize);
		socket.setSoTimeout(timeout);
		return socket;
	}
	
	public static void close(DatagramSocket socket) {
		socket.close();
	}
	
    public static DatagramPacket send(DatagramSocket socket, InetAddress host, int port, byte[] request, int requestLen) throws IOException {
        DatagramPacket packet = new DatagramPacket(request, requestLen, host, port);
        socket.send(packet);
        return packet;
    }
    
    public static DatagramPacket send(DatagramSocket socket, SocketAddress socketAddress, byte[] request, int requestLen) throws IOException {
        DatagramPacket packet = new DatagramPacket(request, requestLen, socketAddress);
        socket.send(packet);
        return packet;
    }

    public static DatagramPacket receive(DatagramSocket socket, byte[] response) throws IOException {
        DatagramPacket packet = new DatagramPacket(response, response.length);
        socket.receive(packet);
        return packet;
    }

}

