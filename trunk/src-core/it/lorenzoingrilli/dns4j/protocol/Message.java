/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

import it.lorenzoingrilli.dns4j.protocol.rr.RR;

import java.util.List;

/**
 * DNS message.
 * 
 * Defined in RFC 1035.
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
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
