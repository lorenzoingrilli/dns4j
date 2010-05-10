package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class MessageBuilder {
	
	private Message message = new MessageImpl();
	
	public MessageBuilder setRecursionDesidered(boolean flag) {
		message.getHeader().setRecursionDesidered(flag);
		return this;
	}
	
	public MessageBuilder setId(int id) {
		message.getHeader().setId(id);
		return this;
	}
	
	public MessageBuilder addQuestion(Question q) {
		message.getQuestions().add(q);
		message.getHeader().setQdCount(message.getHeader().getQdCount()+1);
		return this;
	}
	
	public Message message() {
		return message;
	}
	
}
