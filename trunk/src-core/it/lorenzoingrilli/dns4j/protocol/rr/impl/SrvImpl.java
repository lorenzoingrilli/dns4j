/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.Srv;

/**
 * @author Lorenzo Ingrilli'
 */
public class SrvImpl extends RRSpecificImpl implements Srv {

	private int priority;
	private int weight;
	private String target;
	private int port;
	
    public SrvImpl() {
        super(Clazz.IN, Type.SRV);
    }
    
    public SrvImpl(String name, long ttl, int priority, int weight, String target, int port) {
    	this();
    	setName(name);
    	setTtl(ttl);
    	setPriority(priority);
    	setWeight(weight);
    	setTarget(name);
    	setPort(port);
    }

    @Override
    public String toString() {
        return "SRV(name="+getName()+", ttl="+getTtl()+", priority="+priority+", weight="+weight+", target="+target+", port="+port+")";
    }

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public void setWeight(int weight) {
		this.weight = weight;		
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public void setTarget(String target) {
		this.target = target;
	}

}
