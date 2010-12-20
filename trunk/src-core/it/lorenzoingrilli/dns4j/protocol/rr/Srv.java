/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * SRV Resource Record.
 * 
 * Defined in RFC 2782
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc2782.txt">RFC 2782</a> 
 */
public interface Srv extends RR {	
    public int getPriority();
    public void setPriority(int priority);
    public int getWeight();
    public void setWeight(int weight);
    public int getPort();
    public void setPort(int port);
    public String getTarget();
    public void setTarget(String target);
}
