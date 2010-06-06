package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.A;

import java.net.Inet4Address;

/**
 * @author Lorenzo Ingrilli'
 */
public class AImpl extends RRSpecificImpl implements A {

    private Inet4Address address;

    public AImpl() {
        super(Clazz.IN, Type.A);
    }

    @Override
    public String toString() {
        return "A(name="+getName()+", ttl="+getTtl()+", address="+address.getHostAddress()+")";
    }

    @Override
    public Inet4Address getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Inet4Address address) {
        this.address = address;
    }

}
