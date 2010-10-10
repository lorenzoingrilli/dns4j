/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Question;

/**
 * @author Lorenzo Ingrilli'
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + qclass;
		result = prime * result + ((qname == null) ? 0 : qname.hashCode());
		result = prime * result + qtype;
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
		QuestionImpl other = (QuestionImpl) obj;
		if (qclass != other.qclass)
			return false;
		if (qname == null) {
			if (other.qname != null)
				return false;
		} else if (!qname.equals(other.qname))
			return false;
		if (qtype != other.qtype)
			return false;
		return true;
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
