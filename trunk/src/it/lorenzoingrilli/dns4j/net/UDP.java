package it.lorenzoingrilli.dns4j.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class UDP {

	public static final int DEFAULT_SEND_BUFFER_SIZE = 512;
	public static final int DEFAULT_RECV_BUFFER_SIZE = 512;
	public static final int DEFAULT_TIMEOUT = 5000;
	
	public static DatagramSocket open(int timeout) throws SocketException {
		DatagramSocket socket = new DatagramSocket();
		socket.setSendBufferSize(DEFAULT_SEND_BUFFER_SIZE);
		socket.setReceiveBufferSize(DEFAULT_RECV_BUFFER_SIZE);
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

    public static DatagramPacket receive(DatagramSocket socket, byte[] response) throws IOException {
        DatagramPacket packet = new DatagramPacket(response, response.length);
        socket.receive(packet);
        return packet;
    }

}

