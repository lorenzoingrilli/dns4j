package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.RetCodes;
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
			// TODO if not found return SOA record
		}
		
		return response;
	}
	
	abstract public QuestionResponse query(Question q); 
	
	public boolean isQuestionEcho() {
		return questionEcho;
	}

	public void setQuestionEcho(boolean questionEcho) {
		this.questionEcho = questionEcho;
	}
}

