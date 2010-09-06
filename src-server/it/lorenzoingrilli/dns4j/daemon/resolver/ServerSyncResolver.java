package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

/**
 * Synchronous Resolver
 * 
 * This kind of resolver block until a response (or a failure) was received.
 * 
 * @author Lorenzo Ingrilli'
 */
public interface ServerSyncResolver<T> extends SyncResolver {
	public Message query(Message request);
	public Message query(Message request, T queryContext);
}
