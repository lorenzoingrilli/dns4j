package it.lorenzoingrilli.dns4j.daemon.plugins;

import it.lorenzoingrilli.dns4j.daemon.EventDispatcher;
import it.lorenzoingrilli.dns4j.daemon.EventRecv;
import it.lorenzoingrilli.dns4j.daemon.EventSent;
import it.lorenzoingrilli.dns4j.daemon.Plugin;
import it.lorenzoingrilli.dns4j.net.TCP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

public class TCPServerPlugin implements Runnable, Plugin {

	private ServerSocket ssocket = null;
	private SyncResolver resolver;
	private Executor executor;
	private Serializer serializer;

	private int port;
	private EventDispatcher dispatcher;
	
	@ConstructorProperties(value={"port", "resolver", "executor", "serializer"})
	public TCPServerPlugin(int port, SyncResolver resolver, Executor executor, Serializer serializer) {
		this.resolver = resolver;
		this.executor = executor;
		this.serializer = serializer;
		this.port = port;
	}
	
	@Override
	public void init(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void destroy() {
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
					// WARN: we should manage multiple request on the same tcp connection
					Message request = serializer.deserialize(socket.getInputStream());
					dispatcher.dispatch(new EventRecv(this, request));
					Message response = resolver.query(request);
					if(response==null) return;
					serializer.serialize(response, socket.getOutputStream());
					dispatcher.dispatch(new EventSent(this, response));
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
	
	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

}
