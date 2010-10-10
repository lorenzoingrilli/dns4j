/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.resolver.DnsEventListener;
import it.lorenzoingrilli.dns4j.resolver.AsyncResolver;

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

/**
 * @author Lorenzo Ingrilli'
 */
public class AsyncDNSClient implements AsyncResolver {

	private Serializer serializer = new SerializerImpl();
    private InetAddress address;
    private int port;

    private DatagramSocket socket;
    
    private DnsEventListener eventListener;

    private ConcurrentHashMap<Integer, DelayedRequest> requests = new ConcurrentHashMap<Integer, DelayedRequest>();
    private DelayQueue<DelayedRequest> queue = new DelayQueue<DelayedRequest>();

    public AsyncDNSClient(InetAddress address, int port) throws UnknownHostException, SocketException {
        this.address = address;
        this.port = port;
        socket = new DatagramSocket();
        socket.setSoTimeout(250);
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
            if(listener!=null)
                listener.onRequest(request);
        } catch (IOException ex) {
            if(eventListener!=null)
                eventListener.onException(buffer, ex);
            if(listener!=null)
                listener.onException(buffer, ex);
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
            DelayedRequest req = requests.get(resp.getHeader().getId());
            if(req!=null) {
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

}

class DelayedRequest implements Delayed {

    private Message message = null;
    private DnsEventListener listener;
    private long ts;
    
    public DelayedRequest(Message message, DnsEventListener listener) {
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

}
