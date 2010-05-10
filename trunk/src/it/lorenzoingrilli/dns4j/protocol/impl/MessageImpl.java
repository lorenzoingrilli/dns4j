package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
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
