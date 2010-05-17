package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class RRImpl implements RR {

    private String name;
    private int type;
    private int clazz = Clazz.IN;
    private long ttl;
    private byte[] rdata;

    @Override
    public String toString() {
    	return "RR(name="+name+", type="+type+", class="+clazz+", ttl="+ttl+")";     	
    }
    
    @Override
    public long getTtl() {
        return ttl;
    }

    @Override
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public int getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(int clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public byte[] getRdata() {
        return rdata;
    }

    @Override
    public void setRdata(byte[] rdata) {
        this.rdata = rdata;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getRdLenght() {
        return this.rdata!=null?this.rdata.length:0;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

}
