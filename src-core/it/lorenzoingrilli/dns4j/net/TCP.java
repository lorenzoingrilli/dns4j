/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * TCP helper methods
 * 
 * @author Lorenzo Ingrilli'
 */
public class TCP {

	public static final int DEFAULT_RECV_BUFFER_SIZE = 65536;	
	public static final int DEFAULT_TIMEOUT = 1500;
	public static final int DEFAULT_PORT = 53;
	public static final int DEFAULT_BACKLOG = 100;
	public static final InetAddress DEFAULT_BINDADDRESS = null;
	
    public static int receive(InputStream is, byte[] response) throws IOException {
	 int letti = 0;
	 int r = 0;
	 while(r>=0) {
            r = is.read(response, letti, response.length-letti);
            letti += r;
	 }
	 return letti;
    }

    public static void send(OutputStream os, byte[] request, int len) throws IOException {
        os.write(request, 0, len);
    }
    
    public static int query(InetAddress host, int port, byte[] request, int requestLen, byte[] response) throws IOException {
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
    
    public static ServerSocket server(int port, int backlog, InetAddress bindAddr, int timeout, int recvBufferSize) throws IOException {
    	ServerSocket socket = new ServerSocket(port, backlog, bindAddr);
    	socket.setSoTimeout(timeout);
		socket.setReceiveBufferSize(recvBufferSize);
    	//socket.setPerformancePreferences(connectionTime, latency, bandwidth)
    	return socket;
    }
    
    public static void close(Socket socket) throws IOException {
    	socket.close();
    }

    public static void close(ServerSocket socket) throws IOException {
    	socket.close();
    }
    
}

