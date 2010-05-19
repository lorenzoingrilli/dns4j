package it.lorenzoingrilli.dns4j.cli;

import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.impl.DeserializatorImpl;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializatorImpl;
import it.lorenzoingrilli.dns4j.resolver.impl.YamlResolver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

/**
 * DNS Server
 *
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class nsd {
    
    public static void main(String[] args) throws Exception {
     YamlResolver resolver = new YamlResolver(args[0]);
	 resolver.setQuestionEcho(true);
	 
     DatagramSocket socket = new DatagramSocket(5053);
	 socket.setSoTimeout(500);
	 byte buffer[] = new byte[512];
	 while(true)
	 try
	 {
			 DatagramPacket packet = UDP.receive(socket, buffer);
			 Message request = DeserializatorImpl.deserialize(packet.getData());
			 System.out.println("REQUEST  = "+request);
			 Message response = resolver.query(request);
			 System.out.println("RESPONSE = "+response);
			 int len = SerializatorImpl.serialize(response, buffer);
			 UDP.send(socket, packet.getAddress(), packet.getPort(), buffer, len);
	 }
	 catch(Exception e)
	 {
		 if(! (e instanceof SocketTimeoutException))
			 e.printStackTrace();
	 }
    }
    
}
