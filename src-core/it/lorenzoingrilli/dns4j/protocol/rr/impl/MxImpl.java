/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.Mx;

/**
 * @author Lorenzo Ingrilli'
 */
public class MxImpl extends RRSpecificImpl implements Mx {

	private String exchange;
	private int preference;
	
    public MxImpl() {
        super(Clazz.IN, Type.MX);
    }
    
    public MxImpl(String name, long ttl, String exchange, int preference) {
        this();
        setName(name);
        setTtl(ttl);
        setExchange(exchange);
        setPreference(preference);
    }
    
    @Override
    public String toString() {
        return "MX(name="+getName()+", ttl="+getTtl()+", preference="+preference+", exchange="+exchange+")";
    }
    
	@Override
	public String getExchange() {
		return exchange;
	}

	@Override
	public int getPreference() {
		return preference;
	}

	@Override
	public void setExchange(String exchange) {
		this.exchange = exchange;		
	}

	@Override
	public void setPreference(int preference) {
		this.preference = preference;
	}

}
