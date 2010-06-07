package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * Asynchronuos Resolver.
 * 
 * High performance asynchronuos. You can do multiple request ad obtain these in a event-driven model.  
 * 
 * @author Lorenzo Ingrilli'
 */
public interface AsyncResolver extends Resolver, Runnable {
    public void query(Message request, AsyncEventListener listener);
    public void setEventListener(AsyncEventListener listener);
    public void setUnexpectedResponseListener(AsyncUnexpectedResponseListener listener);
}
