package it.lorenzoingrilli.dns4j.protocol;

import it.lorenzoingrilli.dns4j.protocol.rr.RR;

import java.util.List;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Message {
    public Header getHeader();
    public List<Question> getQuestions();
    public List<RR> getAnswer();
    public List<RR> getAuthority();
    public List<RR> getAdditional();

    public void setHeader(Header header);
    public void setQuestions(List<Question> questions);
    public void setAnswer(List<RR> answer);
    public void setAuthority(List<RR> authority);
    public void setAdditional(List<RR> additional);

}
