/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * SOA Resource Record.
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface Soa extends RR {
    public String getMname();
    public void setMname(String mname);
    public String getRname();
    public void setRname(String rname);
    public long getSerial();
    public void setSerial(long serial);
    public long getRefresh();
    public void setRefresh(long refresh);
    public long getRetry();
    public void setRetry(long retry);
    public long getExpire();
    public void setExpire(long expire);
    public long getMinimum();
    public void setMinimum(long minimum);
}
