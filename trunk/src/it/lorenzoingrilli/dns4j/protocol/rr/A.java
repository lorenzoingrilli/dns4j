package it.lorenzoingrilli.dns4j.protocol.rr;

import java.net.Inet4Address;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface A extends RR {
    public Inet4Address getAddress();
    public void setAddress(Inet4Address address);
}
