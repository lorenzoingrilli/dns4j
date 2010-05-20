package it.lorenzoingrilli.dns4j.cli;

import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.impl.DeserializatorImpl;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializatorImpl;
import it.lorenzoingrilli.dns4j.resolver.impl.YamlResolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * DNS Server
 *
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class nsd {
    
    public static void main(String[] args) throws Exception {
     final YamlResolver resolver = new YamlResolver(args[0]);
	 resolver.setQuestionEcho(true);
	 
	 BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(500, true);
	 ThreadFactory threadFactory = Executors.defaultThreadFactory();
	 ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 500, TimeUnit.MILLISECONDS, queue, threadFactory);

     final DatagramSocket socket = new DatagramSocket(5053);
	 socket.setSoTimeout(500);
	 
	 while(true)
	 try
	 {
		 	final byte buffer[] = new byte[512];
			final DatagramPacket packet = UDP.receive(socket, buffer);
			 executor.execute(new Runnable() {
				 @Override
				 public void run() {
					 Message request = DeserializatorImpl.deserialize(packet.getData());
					 System.out.println("REQUEST  = "+request);
					 Message response = resolver.query(request);
					 System.out.println("RESPONSE = "+response);
					 int len = SerializatorImpl.serialize(response, buffer);
					 try {
						UDP.send(socket, packet.getAddress(), packet.getPort(), buffer, len);
					} catch (IOException e) {
						e.printStackTrace();
					}
				 }
			 });
	 }
	 catch(Exception e)
	 {
		 if(! (e instanceof SocketTimeoutException))
			 e.printStackTrace();
	 }
    }
    
}
