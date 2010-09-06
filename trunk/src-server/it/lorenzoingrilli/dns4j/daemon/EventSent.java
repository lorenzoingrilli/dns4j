package it.lorenzoingrilli.dns4j.daemon;

import it.lorenzoingrilli.dns4j.protocol.Message;

public class EventSent extends EventMessage  {

	public EventSent(Object emitter, Message message, long timestamp) {
		super(emitter, message, timestamp);
	}
	
}
