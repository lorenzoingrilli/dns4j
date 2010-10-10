/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * @author Lorenzo Ingrilli'
 */
public interface DnsEventListener {
    public void onException(byte[] message, Exception e);
    public void onRequest(Message request);
    public void onResponse(Message request, Message response);
    public void onTimeout(Message request);
    public void onUnexpectedResponse(Message response);
}
