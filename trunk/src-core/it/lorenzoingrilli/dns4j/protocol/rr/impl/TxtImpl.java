/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.Txt;

/**
 * @author Lorenzo Ingrilli'
 */
public class TxtImpl extends RRSpecificImpl implements Txt {

    private String data;

	public TxtImpl() {
        super(Clazz.IN, Type.TXT);
    }
	
    public TxtImpl(String name, long ttl, String data) {
        this();
        setName(name);
        setTtl(ttl);
        setData(data);
    }

    @Override
    public String toString() {
        return "TXT(name="+getName()+", ttl="+getTtl()+", data="+data+")";
    }

    @Override
    public String getData() {
		return data;
	}

    @Override
	public void setData(String data) {
		this.data = data;
	}

}
