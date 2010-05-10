package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Question;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class QuestionImpl implements Question {

    private String qname;
    private int qtype;
    private int qclass;

    public QuestionImpl(){    	
    }
    
    public QuestionImpl(String qname, int qtype, int qclass){
    	this.qname = qname;
    	this.qtype = qtype;
    	this.qclass = qclass;
    }
    
    @Override
    public String toString() {
        return "Question(name="+qname+", type="+qtype+", class="+qclass+")";
    }

    @Override
    public String getQname() {
        return qname;
    }

    @Override
    public int getQtype() {
        return qtype;
    }

    @Override
    public int getQclass() {
        return qclass;
    }

    @Override
    public void setQclass(int qclass) {
        this.qclass = qclass;
    }

    @Override
    public void setQname(String qname) {
        this.qname = qname;
    }

    @Override
    public void setQtype(int qtype) {
        this.qtype = qtype;
    }

}
