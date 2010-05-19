package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.AAAA;

import java.net.Inet6Address;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class AAAAImpl extends RRSpecificImpl implements AAAA {

    private Inet6Address address;

    public AAAAImpl() {
        super(Clazz.IN, Type.AAAA);
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
