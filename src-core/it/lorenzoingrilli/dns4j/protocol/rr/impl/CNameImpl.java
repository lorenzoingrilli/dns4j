/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.CName;

/**
 * @author Lorenzo Ingrilli'
 */
public class CNameImpl extends RRSpecificImpl implements CName {

    private String cname;

    public CNameImpl() {
        super(Clazz.IN, Type.CNAME);
    }
    
    public CNameImpl(String name, long ttl, String cname) {
    	this();
    	setName(name);    	
    	setTtl(ttl);
    	setCname(cname);
    }

    @Override
    public String toString() {
        return "CNAME(name="+getName()+", ttl="+getTtl()+", cname="+cname+")";
    }

    @Override
    public String getCname() {
        return this.cname;
    }

    @Override
    public void setCname(String cname) {
        this.cname = cname;
    }

}
