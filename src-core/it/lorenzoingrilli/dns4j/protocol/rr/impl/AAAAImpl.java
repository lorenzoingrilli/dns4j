/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.AAAA;

import java.net.Inet6Address;

/**
 * @author Lorenzo Ingrilli'
 */
public class AAAAImpl extends RRSpecificImpl implements AAAA {

    private Inet6Address address;

    public AAAAImpl() {
        super(Clazz.IN, Type.AAAA);
    }
    
    public AAAAImpl(String name, long ttl, Inet6Address address) {
    	this();
    	setName(name);
    	setTtl(ttl);
    	setAddress(address);    	
    }

    @Override
    public String toString() {
        return "AAAA(name="+getName()+", ttl="+getTtl()+", address="+address.getHostAddress()+")";
    }

    @Override
    public Inet6Address getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Inet6Address address) {
        this.address = address;
    }

}
