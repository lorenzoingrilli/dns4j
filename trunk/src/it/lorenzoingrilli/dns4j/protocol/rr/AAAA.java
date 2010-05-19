package it.lorenzoingrilli.dns4j.protocol.rr;

import java.net.Inet6Address;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface AAAA extends RR {
    public Inet6Address getAddress();
    public void setAddress(Inet6Address address);
}
