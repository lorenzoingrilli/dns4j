/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

/**
 * Header of DNS message.
 * 
 * Defined in RFC 1035.
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface Header {
    public static final boolean QUESTION = false;
    public static final boolean ANSWER = true;

    public int getId();
    public boolean isQuery();
    public int getOpcode();    
    public boolean isAuthoritative();
    public boolean isTruncated();
    public boolean isRecursionDesidered();
    public boolean isRecursionAvailable();
    public int getZ();
    public int getResponseCode();
    public int getQdCount();
    public int getAnCount();
    public int getNsCount();
    public int getArCount();

    public void setId(int id);
    public void setQuery(boolean flag);
    public void setOpcode(int opCode);
    public void setAuthoritative(boolean flag);
    public void setTruncated(boolean flag);
    public void setRecursionDesidered(boolean flag);
    public void setRecursionAvailable(boolean flag);
    public void setZ(int z);
    public void setResponseCode(int responseCode);
    public void setQdCount(int qdcount);
    public void setAnCount(int ancount);
    public void setNsCount(int nscount);
    public void setArCount(int arcount);
}
