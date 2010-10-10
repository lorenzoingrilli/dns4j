/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Lorenzo Ingrilli'
 */
public class MessageImpl implements Message {

	private Header header = new HeaderImpl();
    private List<Question> question = new LinkedList<Question>();
    private List<RR> answer = new LinkedList<RR>();
    private List<RR> authority = new LinkedList<RR>();
    private List<RR> additional = new LinkedList<RR>();

    @Override
    public String toString() {
        return "Message(header="+header+", question="+question+", answer="+answer+", authority"+authority+", additional="+additional+")";
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((additional == null) ? 0 : additional.hashCode());
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result
				+ ((authority == null) ? 0 : authority.hashCode());
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result
				+ ((question == null) ? 0 : question.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageImpl other = (MessageImpl) obj;
		if (additional == null) {
			if (other.additional != null)
				return false;
		} else if (!additional.equals(other.additional))
			return false;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
			return false;
		if (header == null) {
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		return true;
	}

    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public List<Question> getQuestions() {
        return question;
    }

    @Override
    public List<RR> getAnswer() {
        return answer;
    }

    @Override
    public List<RR> getAuthority() {
        return authority;
    }

    @Override
    public List<RR> getAdditional() {
        return additional;
    }

    @Override
    public void setAdditional(List<RR> additional) {
        this.additional = additional;
    }

    @Override
    public void setAnswer(List<RR> answer) {
        this.answer = answer;
    }

    @Override
    public void setAuthority(List<RR> authority) {
        this.authority = authority;
    }

    @Override
    public void setHeader(Header header) {
        this.header = header;
    }

    @Override
    public void setQuestions(List<Question> question) {
        this.question = question;
    }

}
