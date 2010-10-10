/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * Generic Resource Record.
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface RR {

    public String getName();
    public int getType();
    public int getClazz();
    public long getTtl();
    public int getRdLenght();
    public byte[] getRdata();

    public void setName(String name);
    public void setType(int type);
    public void setClazz(int clazz);
    public void setTtl(long ttl);
    public void setRdata(byte[] rdata);
}
