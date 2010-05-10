package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface AsyncResolver extends Resolver, Runnable {
    public void query(Message request, AsyncEventListener listener);
    public void setEventListener(AsyncEventListener listener);
    public void setUnexpectedResponseListener(AsyncUnexpectedResponseListener listener);
}
