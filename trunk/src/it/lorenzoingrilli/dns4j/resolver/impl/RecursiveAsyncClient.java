package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.impl.DeserializatorImpl;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializatorImpl;
import it.lorenzoingrilli.dns4j.resolver.AsyncEventListener;
import it.lorenzoingrilli.dns4j.resolver.AsyncResolver;
import it.lorenzoingrilli.dns4j.resolver.AsyncUnexpectedResponseListener;

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
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class RecursiveAsyncClient implements AsyncResolver {

    private InetAddress address;
    private int port;

    private DatagramSocket socket;
    
    private AsyncEventListener eventListener;
    private AsyncUnexpectedResponseListener unexpectedListener;

    private ConcurrentHashMap<Integer, DelayedRequest> requests = new ConcurrentHashMap<Integer, DelayedRequest>();
    private DelayQueue<DelayedRequest> queue = new DelayQueue<DelayedRequest>();

    public RecursiveAsyncClient(InetAddress address, int port) throws UnknownHostException, SocketException {
        this.address = address;
        this.port = port;
        socket = new DatagramSocket();
        socket.setSoTimeout(250);
    }

    public Message query(Message request) {
        _query(request, null);
        return null;
    }

    public void query(Message request, AsyncEventListener listener) {
        _query(request, listener);
    }

    private void _query(Message request, AsyncEventListener listener) {
        DelayedRequest dr= new DelayedRequest(request, listener);
        requests.put(request.getHeader().getId(), dr);
        queue.add(dr);
        // Invio il messaggio
        byte[] buffer = new byte[512];
        int i = SerializatorImpl.serialize(request, buffer);
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
            Message resp = DeserializatorImpl.deserialize(buffer);
            DelayedRequest req = requests.get(resp.getHeader().getId());
            if(req!=null) {
                requests.remove(resp.getHeader().getId());
                if(req.getEventListener()!=null)
                    req.getEventListener().onResponse(req.getMessage(), resp);
                if(eventListener!=null)
                    eventListener.onResponse(req.getMessage(), resp);
            }
            else if(req==null && unexpectedListener!=null) {
                unexpectedListener.onUnexpectedResponse(resp);
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
    public void setEventListener(AsyncEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void setUnexpectedResponseListener(AsyncUnexpectedResponseListener listener) {
        this.unexpectedListener = listener;
    }

}

class DelayedRequest implements Delayed {

    private Message message = null;
    private AsyncEventListener listener;
    private long ts;
    
    public DelayedRequest(Message message, AsyncEventListener listener) {
        this.ts = System.currentTimeMillis();
        this.message = message;
        this.listener = listener;
    }

    public Message getMessage() {
        return message;
    }

    public AsyncEventListener getEventListener() {
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
