/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
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
    public void asyncQuery(Message request, DnsEventListener listener);
    public void setEventListener(DnsEventListener listener);
}
