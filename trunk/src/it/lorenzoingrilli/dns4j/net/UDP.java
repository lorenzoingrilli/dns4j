package it.lorenzoingrilli.dns4j.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class UDP {

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

