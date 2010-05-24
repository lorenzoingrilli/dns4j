package it.lorenzoingrilli.dns4j.net;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.impl.DeserializatorImpl;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializatorImpl;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class TCPServer implements Runnable {

	private int timeout = UDP.DEFAULT_TIMEOUT;
	private ServerSocket ssocket = null;
	private SyncResolver resolver;
	private Executor executor;
	private int port;
	
	public TCPServer() { }
	
	public TCPServer(int port, SyncResolver resolver, Executor executor) {
		this.resolver = resolver;
		this.executor = executor;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			ssocket = TCP.server(port, 100, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
				
		while(!Thread.interrupted())
		try
		{
			final Socket socket = ssocket.accept();			
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
					// WARN: we shuold manage multiple request on the same tco connection
					InputStream is = socket.getInputStream();
					byte[] array = new byte[2];
					is.read(array);
					ByteBuffer bb = ByteBuffer.wrap(array);					
					int len = DeserializatorImpl.getUShort(bb);
					final byte buffer[] = new byte[len];
					
					 int letti = 0;
					 int r = 0;
					 while(r>=0 && letti<len) {
				            r = is.read(buffer, letti, buffer.length-letti);
				            letti += r;
				            System.out.println(letti);
					 }
					 
					Message request = DeserializatorImpl.deserialize(buffer);
					System.out.println("REQUEST  = "+request);
					Message response = resolver.query(request);
					System.out.println("RESPONSE = "+response);
					byte[] resp = new byte[TCP.DEFAULT_BUFFER_SIZE];
					bb.rewind();
					len = SerializatorImpl.serialize(response, resp);
					SerializatorImpl.putUShort(bb, len);
					OutputStream os = socket.getOutputStream();
					os.write(array);
					os.write(resp);
					//socket.close();
					}
					catch(IOException e) {
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
		
		try {
			TCP.close(ssocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public SyncResolver getResolver() {
		return resolver;
	}

	public void setResolver(SyncResolver resolver) {
		this.resolver = resolver;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
