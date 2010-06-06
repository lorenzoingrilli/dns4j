package it.lorenzoingrilli.dns4j.protocol.rr.impl;

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
