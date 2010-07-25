package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.RetCodes;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageImpl;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
abstract public class AuthoritativeResolver implements SyncResolver {
		
	private boolean questionEcho = false;

	@Override
	public Message query(Message request) {
		Message response = new MessageImpl();
		response.getHeader().setId(request.getHeader().getId());
		response.getHeader().setRecursionDesidered(request.getHeader().isRecursionDesidered());
		response.getHeader().setRecursionAvailable(false);
		response.getHeader().setAuthoritative(true);
		response.getHeader().setQuery(Header.ANSWER);
				
		for(Question q: request.getQuestions()) {
			QuestionResponse r = query(q);
			if(r!=null) {
				response.getAnswer().addAll(r.getAnswer());
				response.getAuthority().addAll(r.getAuthority());
				response.getAdditional().addAll(r.getAdditional());
			}
		}
		if(questionEcho)
			response.getQuestions().addAll(request.getQuestions());
		
		if(response.getAnswer().size()==0) {
			response.getHeader().setResponseCode(RetCodes.NOTFOUND);			
			for(Question q: request.getQuestions()) {
				int n = q.getQname().indexOf('.');
				String zonename = q.getQname().substring(n+1);
				QuestionResponse r = query(zonename, q.getQclass(), Type.SOA);
				if(r!=null) {
					response.getAnswer().addAll(r.getAnswer());
					response.getAuthority().addAll(r.getAuthority());
					response.getAdditional().addAll(r.getAdditional());
				}
				else {
					System.out.println("soa not found");
				}
			}
		}
		
		response.getHeader().setQdCount(response.getQuestions().size());
		response.getHeader().setAnCount(response.getAnswer().size());
		response.getHeader().setNsCount(response.getAuthority().size());
		response.getHeader().setArCount(response.getAdditional().size());
		
		return response;
	}
	
	public QuestionResponse query(Question q) {
		return query(q.getQname(), q.getQclass(), q.getQtype());
	}
	
	abstract public QuestionResponse query(String qname, int qclass, int qtype);
	
	public boolean isQuestionEcho() {
		return questionEcho;
	}

	public void setQuestionEcho(boolean questionEcho) {
		this.questionEcho = questionEcho;
	}
}

