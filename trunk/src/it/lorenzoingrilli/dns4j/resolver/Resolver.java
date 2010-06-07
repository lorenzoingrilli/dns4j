package it.lorenzoingrilli.dns4j.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * Base Resolver
 * 
 * @author Lorenzo Ingrilli'
 */
public interface Resolver {
    public Message query(Message request);
}
