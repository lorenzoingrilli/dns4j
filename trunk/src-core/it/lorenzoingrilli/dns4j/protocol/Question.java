/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

/**
 * Question part of DNS message.
 * 
 * Defined in RFC 1035.
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface Question {
    public String getQname();
    public int getQtype();
    public int getQclass();

    public void setQname(String qname);
    public void setQtype(int type);
    public void setQclass(int qclass);
}
