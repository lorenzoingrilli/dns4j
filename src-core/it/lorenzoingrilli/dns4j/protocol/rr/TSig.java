/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * TSIG Resource Record.
 * 
 * Defined in RFC 2845
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="http://tools.ietf.org/html/rfc2845">RFC 2845</a> 
 */
public interface TSig extends RR {	
    public String getAlgorithm();
    public void setAlgorithm(String algorithm);
    public long getTime();
    public void setTime(long time);
    public long getFudge();
    public void setFudge(long fudge);
    public long getMacSize();
    public void setMacSize(long macSize);
    public byte[] getMac();
    public void setMac(byte[] mac);
    public long getOriginalId();
    public void setOriginalId(long originalId);
    public long getError();
    public void setError(long error);
    public long getOtherDataLen();
    public void setOtherDataLen(long otherDataLen);
    public byte[] getOtherData();
    public void setOtherData(byte[] otherData);
}
