/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon;

import it.lorenzoingrilli.dns4j.protocol.Message;

public class EventMessage implements Event {

	private Message message;
	private Object emitter;
	private long timestamp;

	public EventMessage(Object emitter, Message message, long timestamp) {
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
