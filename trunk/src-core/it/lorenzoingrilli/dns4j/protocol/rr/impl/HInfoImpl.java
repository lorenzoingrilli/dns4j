/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.HInfo;

/**
 * @author Lorenzo Ingrilli'
 */
public class HInfoImpl extends RRSpecificImpl implements HInfo {

    private String host;
    private String cpu;

	public HInfoImpl() {
        super(Clazz.IN, Type.HINFO);
    }
	
    public HInfoImpl(String name, long ttl, String host, String cpu) {
    	this();
    	setName(name);    	
    	setTtl(ttl);
    	setHost(host);
    	setCpu(cpu);
    }

    @Override
    public String toString() {
        return "HINFO(name="+getName()+", ttl="+getTtl()+", cpu="+cpu+", host="+host+")";
    }

    @Override
    public String getHost() {
		return host;
	}

    @Override
	public void setHost(String host) {
		this.host = host;
	}

    @Override
	public String getCpu() {
		return cpu;
	}

    @Override
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

}
