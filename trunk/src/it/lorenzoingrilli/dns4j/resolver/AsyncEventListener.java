package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface AsyncEventListener {
    public void onException(byte[] message, Exception e);
    public void onRequest(Message request);
    public void onResponse(Message request, Message response);
    public void onTimeout(Message request);
}
