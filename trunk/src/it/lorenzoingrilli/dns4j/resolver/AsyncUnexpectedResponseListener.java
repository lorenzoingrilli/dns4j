package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface AsyncUnexpectedResponseListener {
    public void onUnexpectedResponse(Message response);
}
