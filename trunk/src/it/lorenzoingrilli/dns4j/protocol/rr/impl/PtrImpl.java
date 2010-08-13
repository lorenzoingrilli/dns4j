package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.Ptr;

/**
 * @author Lorenzo Ingrilli'
 */
public class PtrImpl extends RRSpecificImpl implements Ptr {

    private String ptrDname;

    public PtrImpl() {
        super(Clazz.IN, Type.PTR);
    }
    
    public PtrImpl(String name, String ptrdname, long ttl) {
    	this();
    	setName(name);
    	setPtrDname(ptrdname);
    	setTtl(ttl);
    }
    
    public static InetAddress nameToAddress(String name) throws UnknownHostException {
		int i = name.lastIndexOf(".in-addr.arpa");
		String f[] = name.substring(0, i).split("\\.");
		String addr = f[3]+"."+f[2]+"."+f[1]+"."+f[0];
		return InetAddress.getByName(addr);
    }

    @Override
    public String toString() {
        return "PTR(name="+getName()+", cname="+ptrDname+", ttl="+getTtl()+")";
    }

    @Override
    public String getPtrDname() {
        return this.ptrDname;
    }

    @Override
    public void setPtrDname(String cname) {
        this.ptrDname = cname;
    }

}
