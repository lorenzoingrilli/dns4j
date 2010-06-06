package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import java.util.Random;

/**
 * @author Lorenzo Ingrilli'
 */
public class MessageBuilder {
	
	private Message message = null;
        private Random random = null;

        public MessageBuilder() {
            message = new MessageImpl();
            random = new Random(System.currentTimeMillis());
            setId(random.nextInt(65535));
        }
	
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
