package it.lorenzoingrilli.dns4j.protocol.rr.impl;

import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.A;

import java.net.Inet4Address;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class AImpl extends RRSpecificImpl implements A {

    private Inet4Address address;

    public AImpl() {
        super(Type.A);
    }

    @Override
    public String toString() {
        return "A(name="+getName()+", address="+address.getHostAddress()+")";
    }

    public Inet4Address getAddress() {
        return this.address;
    }

    public void setAddress(Inet4Address address) {
        this.address = address;
    }

}
