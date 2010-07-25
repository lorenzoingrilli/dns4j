package it.lorenzoingrilli.dns4j.daemon;

import it.lorenzoingrilli.dns4j.protocol.Message;

public class EventMessage implements Event {

	private Message message;
	private Object emitter;

	public EventMessage(Object emitter, Message message) {
		super();
		this.emitter = emitter;
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
	
	public Object getEmitter() {
		return emitter;
	}
	
}
