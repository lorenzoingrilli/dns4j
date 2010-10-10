/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;

import java.util.Collection;
import java.util.Random;

/**
 * DNS message generator helper
 * 
 * Be careful: this class is not thread safe.
 * 
 * @author Lorenzo Ingrilli'
 */
public class MessageBuilder {
	
	/** Message transaction id is between 0 and 65535 */
	private static final int MAX_ID = 65535;
	
	/** Random number generation (used to generate random id)*/
	private Random random = new Random();
	
	private Message message = null;
	
	public MessageBuilder() {		
		create();
	}
	
	public MessageBuilder create() {
		message = new MessageImpl();
		randomId();
		return this;
	}
	
	/** Return the composed message */
	public Message message() {
		return message;
	}
	
	/** Set random transaction id in header */
	public MessageBuilder randomId() {
		id(random.nextInt(MAX_ID));
		return this;
	}

	
	/** Set transaction id in header */
	public MessageBuilder id(int id) {
		message.getHeader().setId(id);
		return this;
	}
	
	/** Set transaction id as in input message */
	public MessageBuilder id(Message message) {
		return id(message.getHeader().getId());
	}
	
	/** Add a question in the questions part */
	public MessageBuilder addQuestion(Question q) {
		message.getQuestions().add(q);
		message.getHeader().setQdCount(message.getHeader().getQdCount()+1);
		return this;
	}
	
	/** Add a question list in the questions part */
	public MessageBuilder addQuestions(Collection<Question> questions) {
		for(Question q: questions)
			addQuestion(q);
		return this;
	}
	
	/** Add a question list as in input message */
	public MessageBuilder addQuestions(Message message) {
		return addQuestions(message.getQuestions());
	}
	
	/** Add a question in the questions part */
	public MessageBuilder addQuestion(String qname, int qtype, int qclass) {		
		return this.addQuestion(new QuestionImpl(qname, qtype, qclass));
	}
	
	/** Add a question in the questions part (class INET) */
	public MessageBuilder addQuestion(String qname, int qtype) {		
		return this.addQuestion(new QuestionImpl(qname, qtype, Clazz.IN));
	}
	
	/** Add a question in the questions part (class INET, type A) */
	public MessageBuilder addQuestion(String qname) {		
		return this.addQuestion(new QuestionImpl(qname, Type.A, Clazz.IN));
	}
	
	/** Add a resource record in answer part*/
	public MessageBuilder addAnswer(RR rr) {
		message.getAnswer().add(rr);
		message.getHeader().setAnCount(message.getHeader().getAnCount()+1);
		return this;
	}
	
	/** Add a resource record in authority part*/
	public MessageBuilder addAuthority(RR rr) {
		message.getAuthority().add(rr);
		message.getHeader().setNsCount(message.getHeader().getNsCount()+1);
		return this;
	}
	
	/** Add a resource record in additional part*/
	public MessageBuilder addAdditional(RR rr) {
		message.getAdditional().add(rr);
		message.getHeader().setArCount(message.getHeader().getArCount()+1);
		return this;
	}

	/** Set recursion desidered (RD) flag */
	public MessageBuilder recursionDesidered(boolean flag) {
		message.getHeader().setRecursionDesidered(flag);
		return this;
	}
	
	/** Set recursion desidered (RD) flag as in input message */
	public MessageBuilder recursionDesidered(Message message) {
		return recursionDesidered(message.getHeader().isRecursionDesidered());
	}
		
	/** Set recursion desidered (RD) flag to TRUE */
	public MessageBuilder recursionDesidered() {
		return recursionDesidered(true);
	}
	
	/** Set recursion desidered (RD) flag to FALSE */
	public MessageBuilder noRecursionDesidered() {
		return recursionDesidered(false);
	}
	
	/** Set authoritative answer (AA) flag */
	public MessageBuilder authoritative(boolean flag) {
		message.getHeader().setAuthoritative(flag);
		return this;
	}
	
	/** Set authoritative answer (AA) flag to TRUE */
	public MessageBuilder authoritative() {		
		return authoritative(true);
	}
	
	/** Set authoritative answer (AA) flag to FALSE */
	public MessageBuilder notAuthoritative() {		
		return authoritative(false);
	}
	
	/** Set opcode field */
	public MessageBuilder opcode(int opcode) {
		message.getHeader().setOpcode(opcode);
		return this;
	}
	
	/** Set query flag (false=QUESTION, true=ANSWER) */
	public MessageBuilder query(boolean flag) {
		message.getHeader().setQuery(flag);
		return this;
	}
	
	/** Set query flag to QUESTION */
	public MessageBuilder question() {
		return query(Header.QUESTION);
	}
	
	/** Set query flag to ANSWER */
	public MessageBuilder answer() {
		return query(Header.ANSWER);
	}
	
	/** Set query flag to ANSWER and copy questions, rd flag, id from input message */
	public MessageBuilder answer(Message message) {
		query(Header.ANSWER);
		id(message);
		addQuestions(message);
		recursionDesidered(message);
		return this;
	}
	
	/** Set recursion available (RA) flag */
	public MessageBuilder recursionAvailable(boolean flag) {
		message.getHeader().setRecursionAvailable(flag);
		return this;
	}
	
	/** Set recursion available (RA) flag to TRUE */
	public MessageBuilder recursionAvailable() {
		return recursionAvailable(true);
	}
	
	/** Set recursion available (RA) flag to FALSE */
	public MessageBuilder noRecursionAvailable() {
		return recursionAvailable(false);
	}
	
	/** Set response code field */
	public MessageBuilder responseCode(int responseCode) {
		message.getHeader().setResponseCode(responseCode);
		return this;
	}
	
	/** Set truncation (TC) flag */
	public MessageBuilder truncated(boolean flag) {
		message.getHeader().setTruncated(flag);
		return this;
	}
	
	/** Set recursion available (RA) flag to TRUE */
	public MessageBuilder truncated() {
		return truncated(true);
	}
	
	/** Set recursion available (RA) flag to FALSE */
	public MessageBuilder notTruncated() {
		return truncated(false);
	}
	
	/** Set Z field */
	public MessageBuilder z(int z) {
		message.getHeader().setZ(z);
		return this;
	}

}
