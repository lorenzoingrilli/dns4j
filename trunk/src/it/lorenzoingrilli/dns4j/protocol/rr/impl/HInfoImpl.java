package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.HInfo;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class HInfoImpl extends RRSpecificImpl implements HInfo {

    private String host;
    private String cpu;

	public HInfoImpl() {
        super(Clazz.IN, Type.HINFO);
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
