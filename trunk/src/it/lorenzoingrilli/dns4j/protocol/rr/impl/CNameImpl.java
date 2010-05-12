package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.CName;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class CNameImpl extends RRSpecificImpl implements CName {

    private String cname;

    public CNameImpl() {
        super(Type.CNAME);
    }

    @Override
    public String toString() {
        return "CName(name="+getName()+", cname="+cname+")";
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