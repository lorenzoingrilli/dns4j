package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * Asynchronuos Resolver.
 * 
 * High performance asynchronous resolver.
 * You can do multiple request ad in a event-driven model.  
 * 
 * @author Lorenzo Ingrilli'
 */
public interface AsyncResolver extends Resolver, Runnable {
	public void asyncQuery(Message request);
    public void asyncQuery(Message request, AsyncEventListener listener);
    public void setEventListener(AsyncEventListener listener);
}
