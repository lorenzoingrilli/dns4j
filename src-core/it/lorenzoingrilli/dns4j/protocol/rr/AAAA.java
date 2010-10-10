/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr;

import java.net.Inet6Address;

/**
 * AAAA Resource Record.
 * 
 * Defined in RFC 3596
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc3596.txt">RFC 3596</a>
 */
public interface AAAA extends RR {
    public Inet6Address getAddress();
    public void setAddress(Inet6Address address);
}
