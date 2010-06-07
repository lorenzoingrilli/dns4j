package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;

import java.util.Random;

/**
 * Helper to compose DNS message
 * 
 * @author Lorenzo Ingrilli'
 */
public class MessageBuilder {
	
	private static final int MAX_ID = 65535;
		
	private Message message = null;
	private Random random = null;

	public MessageBuilder() {		
		random = new Random(System.currentTimeMillis());
		create();
	}
	
	public MessageBuilder create() {
		message = new MessageImpl();
		setId(random.nextInt(MAX_ID));
		return this;
	}
	
	public Message message() {
		return message;
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
	
	public MessageBuilder addQuestion(String qname, int qtype, int qclass) {		
		return this.addQuestion(new QuestionImpl(qname, qtype, qclass));
	}
	
	public MessageBuilder addAnswer(RR rr) {
		message.getAnswer().add(rr);
		message.getHeader().setAnCount(message.getHeader().getAnCount()+1);
		return this;
	}
	
	public MessageBuilder addAuthority(RR rr) {
		message.getAuthority().add(rr);
		message.getHeader().setNsCount(message.getHeader().getNsCount()+1);
		return this;
	}
	
	public MessageBuilder addAdditional(RR rr) {
		message.getAdditional().add(rr);
		message.getHeader().setArCount(message.getHeader().getArCount()+1);
		return this;
	}

	public MessageBuilder recursionDesidered(boolean flag) {
		message.getHeader().setRecursionDesidered(flag);
		return this;
	}
	
	public MessageBuilder recursionDesidered() {
		return recursionDesidered(true);
	}
	
	public MessageBuilder noRecursionDesidered() {
		return recursionDesidered(false);
	}
	
	public MessageBuilder authoritative(boolean flag) {
		message.getHeader().setAuthoritative(flag);
		return this;
	}
	
	public MessageBuilder authoritative() {		
		return authoritative(true);
	}
	
	public MessageBuilder notAuthoritative() {		
		return authoritative(false);
	}
	
	public MessageBuilder opcode(int opcode) {
		message.getHeader().setOpcode(opcode);
		return this;
	}
	
	public MessageBuilder query(boolean flag) {
		message.getHeader().setQuery(flag);
		return this;
	}
	
	public MessageBuilder question() {
		return query(Header.QUESTION);
	}
	
	public MessageBuilder answer() {
		return query(Header.ANSWER);
	}
	
	public MessageBuilder recursionAvailable(boolean flag) {
		message.getHeader().setRecursionAvailable(flag);
		return this;
	}
	
	public MessageBuilder recursionAvailable() {
		return recursionAvailable(true);
	}
	
	public MessageBuilder noRecursionAvailable() {
		return recursionAvailable(false);
	}
	
	public MessageBuilder responseCode(int responseCode) {
		message.getHeader().setResponseCode(responseCode);
		return this;
	}
	
	public MessageBuilder truncated(boolean flag) {
		message.getHeader().setTruncated(flag);
		return this;
	}
	
	public MessageBuilder truncated() {
		return truncated(true);
	}
	
	public MessageBuilder notTruncated() {
		return truncated(false);
	}
	
	public MessageBuilder z(int z) {
		message.getHeader().setZ(z);
		return this;
	}
		
}
