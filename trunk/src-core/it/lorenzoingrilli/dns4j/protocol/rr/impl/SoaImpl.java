/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.Soa;

/**
 * @author Lorenzo Ingrilli'
 */
public class SoaImpl extends RRSpecificImpl implements Soa {

    private String mname;
    private String rname;
    private long serial;
    private long refresh;
    private long retry;
    private long expire;
    private long minimum;

	public SoaImpl() {
        super(Clazz.IN, Type.SOA);
    }
	
	public SoaImpl(String name, long ttl, String mname, String rname, long serial, long refresh, long retry, long expire, long minimum) {
        this();
        setName(name);
        setTtl(ttl);
        setMname(mname);
        setRname(rname);
        setSerial(serial);
        setRefresh(refresh);
        setRetry(retry);
        setExpire(expire);
        setMinimum(minimum);
    }

    @Override
    public String toString() {
        return "SOA(name="+getName()+", mname="+mname+", rname="+rname+", ttl="+getTtl()+")";
    }
    
    @Override
    public String getMname() {
		return mname;
	}

    @Override
	public void setMname(String mname) {
		this.mname = mname;
	}

    @Override
	public String getRname() {
		return rname;
	}

    @Override
	public void setRname(String rname) {
		this.rname = rname;
	}

    @Override
	public long getSerial() {
		return serial;
	}

    @Override
	public void setSerial(long serial) {
		this.serial = serial;
	}

    @Override
	public long getRefresh() {
		return refresh;
	}

    @Override
	public void setRefresh(long refresh) {
		this.refresh = refresh;
	}

    @Override
	public long getRetry() {
		return retry;
	}

    @Override
	public void setRetry(long retry) {
		this.retry = retry;
	}

    @Override
	public long getExpire() {
		return expire;
	}

    @Override
	public void setExpire(long expire) {
		this.expire = expire;
	}

    @Override
	public long getMinimum() {
		return minimum;
	}

    @Override
	public void setMinimum(long minimum) {
		this.minimum = minimum;
	}


}
