package it.lorenzoingrilli.dns4j.daemon;

import it.lorenzoingrilli.dns4j.protocol.Message;

public class EventRecv extends EventMessage  {

	public EventRecv(Object emitter, Message message) {
		super(emitter, message);
	}
	
}
