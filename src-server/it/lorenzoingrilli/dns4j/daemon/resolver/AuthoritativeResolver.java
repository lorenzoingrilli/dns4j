/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.resolver;

import java.util.Collection;
import java.util.LinkedList;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.RetCodes;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.CName;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;

/**
 * @author Lorenzo Ingrilli'
 */
abstract public class AuthoritativeResolver implements ServerSyncResolver<ServerQueryContext> {
		 
	private static final int DEFAULT_TTL = 86400;
	
	private int defaultTtl	= DEFAULT_TTL;
	private boolean questionEcho = false;	
	
	@Override
	public Message query(Message request, ServerQueryContext queryContext) {
		return query(request);
	}
	
	@Override
	public Message query(Message request)
	{
		Header reqHeader = request.getHeader();
		// process only queries. Packets with no question bit are discarded 
		if(reqHeader.isQuery()!=Header.QUESTION)
			return null;
		// process only packets with response code equals to 0 
		if(reqHeader.getResponseCode()!=0)
			return null;
		// process only packets with not truncation bit 
		if(reqHeader.isTruncated())
			return null;
		
		Message response = new MessageImpl();
		Header respHeader = response.getHeader(); 
		respHeader.setId(reqHeader.getId());
		respHeader.setRecursionDesidered(reqHeader.isRecursionDesidered());
		respHeader.setRecursionAvailable(false);
		respHeader.setAuthoritative(true);
		respHeader.setQuery(Header.ANSWER);
		
		if(questionEcho)
			response.getQuestions().addAll(request.getQuestions());
		
		for(Question q: request.getQuestions()) {			
			response.getAnswer().addAll(query(q));
			if(q.getQtype()==Type.A || q.getQtype()==Type.AAAA ) {
				response.getAnswer().addAll(query(q.getQname(), q.getQclass(), Type.CNAME));
			}
		}
		
		if(response.getAnswer().size()==0) {
			respHeader.setResponseCode(RetCodes.NOTFOUND);			
			for(Question q: request.getQuestions()) {
				int n = q.getQname().indexOf('.');
				String zonename = q.getQname().substring(n+1);
				Collection<RR> rrs = query(zonename, q.getQclass(), Type.SOA);
				response.getAnswer().addAll(rrs);
			}
		}
		else {
			// TODO: cname recursion resolving
			LinkedList<RR> adds = new LinkedList<RR>();
			for(RR rr: response.getAnswer()) {
				if(rr.getType()==Type.CNAME) {
					String alias = ((CName) rr).getCname();
					adds.addAll(query(alias, Clazz.IN, Type.A));
					adds.addAll(query(alias, Clazz.IN, Type.AAAA));
				}
			}
			response.getAnswer().addAll(adds);
		}
		
		respHeader.setQdCount(response.getQuestions().size());
		respHeader.setAnCount(response.getAnswer().size());
		respHeader.setNsCount(response.getAuthority().size());
		respHeader.setArCount(response.getAdditional().size());
		
		return response;
	}
	
	public Collection<RR> query(Question q) {
		return query(q.getQname(), q.getQclass(), q.getQtype());
	}
	
	abstract public Collection<RR> query(String qname, int qclass, int qtype);
	
	public boolean isQuestionEcho() {
		return questionEcho;
	}

	public void setQuestionEcho(boolean questionEcho) {
		this.questionEcho = questionEcho;
	}

	public int getDefaultTtl() {
		return defaultTtl;
	}

	public void setDefaultTtl(int defaultTtl) {
		this.defaultTtl = defaultTtl;
	}
}

