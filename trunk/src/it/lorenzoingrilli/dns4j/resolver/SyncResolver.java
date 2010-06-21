package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * Synchronous Resolver
 * 
 * This kind of resolver block until a response (or a failure) was received.
 * 
 * @author Lorenzo Ingrilli'
 */
public interface SyncResolver extends Resolver {
	public Message query(Message request);
}
