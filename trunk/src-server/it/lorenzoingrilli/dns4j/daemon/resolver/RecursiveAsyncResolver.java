/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.daemon.Plugin;
import it.lorenzoingrilli.dns4j.daemon.Kernel;
import it.lorenzoingrilli.dns4j.daemon.util.Sperimental;
import it.lorenzoingrilli.dns4j.net.UDP;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.resolver.DnsEventListener;
import it.lorenzoingrilli.dns4j.resolver.AsyncResolver;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lorenzo Ingrilli'
 */
@Sperimental
public class RecursiveAsyncResolver
implements AsyncResolver, Plugin {

	public static final LogEventListener LOG_EVENT_LISTENER = null; //new LogEventListener();
	
    private InetAddress address;
    private int port;
    private int timeout = 3000;

    private DnsEventListener eventListener = LOG_EVENT_LISTENER;
    
	private Serializer serializer = new SerializerImpl();
    private DatagramSocket socket;

    private ConcurrentHashMap<Integer, DelayedRequest> requests = new ConcurrentHashMap<Integer, DelayedRequest>();
    private DelayQueue<DelayedRequest> queue = new DelayQueue<DelayedRequest>();

    @ConstructorProperties(value={"timeout"})
    public RecursiveAsyncResolver(int timeout) throws UnknownHostException, SocketException {
    	socket = UDP.open(timeout, UDP.DEFAULT_SEND_BUFFER_SIZE, UDP.DEFAULT_RECV_BUFFER_SIZE);
   }
	
	@Override
	public void asyncQuery(Message request) {
		asyncQuery(request, null);
	}
	
	@Override
	public void asyncQuery(Message request, DnsEventListener listener) {
        DelayedRequest dr= new DelayedRequest(request, listener);
        requests.put(request.getHeader().getId(), dr);
        queue.add(dr);
        // Invio il messaggio
        byte[] buffer = new byte[512];
        int i = serializer.serialize(request, buffer);
        DatagramPacket packet = new DatagramPacket(buffer, i, address, port);
        try {
            socket.send(packet);
            if(eventListener!=null)
                eventListener.onRequest(request);
        } catch (IOException ex) {
            if(eventListener!=null)
                eventListener.onException(buffer, ex);
        }
	}

    @Override
    public void run() {
        byte[] buffer = new byte[512];
        while(!Thread.currentThread().isInterrupted())
        try
        {
            // Elimino le richieste in timeout
            DelayedRequest r = null;
            while((r = queue.poll()) != null) {
                requests.remove(r.getMessage().getHeader().getId());
                if(eventListener!=null)
                    eventListener.onTimeout(r.getMessage());
                if(r.getEventListener()!=null)
                    r.getEventListener().onTimeout(r.getMessage());
            }
            // Leggo messaggi dal socket
            DatagramPacket pResp = new DatagramPacket(buffer, buffer.length);
            socket.receive(pResp);
            Message resp = serializer.deserialize(pResp.getData(), pResp.getOffset(), pResp.getLength());
            //System.out.println("RECV "+resp.getHeader().getId());
            DelayedRequest req = requests.get(resp.getHeader().getId());
            if(req!=null) {
            	queue.remove(req);
                requests.remove(resp.getHeader().getId());
                if(req.getEventListener()!=null)
                    req.getEventListener().onResponse(req.getMessage(), resp);
                if(eventListener!=null)
                    eventListener.onResponse(req.getMessage(), resp);
            }
            else if(req==null && eventListener!=null) {
            	eventListener.onUnexpectedResponse(resp);
            }            
        }
        catch(SocketTimeoutException e) {
        }
        catch(Exception e) {
            if(eventListener!=null)
                eventListener.onException(buffer, e);
        }
    }

    @Override
    public void setEventListener(DnsEventListener eventListener) {
        this.eventListener = eventListener;
    }

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void init(Kernel kernel) {
	}

	@Override
	public void destroy() {
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}

class DelayedRequest implements Delayed {

	private static long idCounter = Long.MIN_VALUE;
	
	private long id;
    private Message message = null;
    private DnsEventListener listener;
    private long ts;
    
    public DelayedRequest(Message message, DnsEventListener listener) {
    	this.id=idCounter++;
        this.ts = System.currentTimeMillis();
        this.message = message;
        this.listener = listener;
    }

    public Message getMessage() {
        return message;
    }

    public DnsEventListener getEventListener() {
        return listener;
    }
    
    public long getDelay(TimeUnit unit) {
	long res = ts+2500L-System.currentTimeMillis();
        return unit.convert(res, TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed o) {
        long t1 = o.getDelay(TimeUnit.MILLISECONDS);
	long t2 = getDelay(TimeUnit.MILLISECONDS);

	if(t2<t1) return -1;
	if(t2==t1) return 0;
	return 1;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelayedRequest other = (DelayedRequest) obj;
		if (id != other.id)
			return false;
		return true;
	}
}

class LogEventListener implements DnsEventListener {
	private static Logger logger = Logger.getLogger(LogEventListener.class.getName());

	@Override
	public void onUnexpectedResponse(Message response) {
		logger.log(Level.INFO, "unexpected "+response);
	}

	@Override
	public void onException(byte[] message, Exception e) {
		logger.log(Level.WARNING, "exception", e);
	}

	@Override
	public void onRequest(Message request) {
		logger.log(Level.INFO, "request "+request);		
	}

	@Override
	public void onResponse(Message request, Message response) {
		logger.log(Level.INFO, "response "+response);		
	}

	@Override
	public void onTimeout(Message request) {
		logger.log(Level.INFO, "timeout "+request);
	}
	
}
