package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.protocol.rr.RR;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Lorenzo Ingrilli'
 */
public class QuestionResponse {
	private List<RR> answer = new LinkedList<RR>();
	private List<RR> authority = new LinkedList<RR>();
	private List<RR> additional = new LinkedList<RR>();
	
	public List<RR> getAnswer() {
		return answer;
	}
	public void setAnswer(List<RR> answer) {
		this.answer = answer;
	}
	public List<RR> getAuthority() {
		return authority;
	}
	public void setAuthority(List<RR> authority) {
		this.authority = authority;
	}
	public List<RR> getAdditional() {
		return additional;
	}
	public void setAdditional(List<RR> additional) {
		this.additional = additional;
	}
	
}
