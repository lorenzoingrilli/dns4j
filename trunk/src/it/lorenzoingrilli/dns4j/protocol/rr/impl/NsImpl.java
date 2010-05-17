package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.Ns;

public class NsImpl extends RRSpecificImpl implements Ns {
	
	private String nsdName;
	
    public NsImpl() {
        super(Type.NS);
    }
    
    @Override
    public String toString() {
        return "NS(name="+getName()+", nsdname="+nsdName+", ttl="+getTtl()+")";
    }

    @Override
	public String getNsdName() {
		return nsdName;
	}

    @Override
	public void setNsdName(String nsdName) {
		this.nsdName = nsdName;
	}


}
