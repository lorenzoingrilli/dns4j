package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.Txt;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class TxtImpl extends RRSpecificImpl implements Txt {

    private String data;

	public TxtImpl() {
        super(Type.TXT);
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
