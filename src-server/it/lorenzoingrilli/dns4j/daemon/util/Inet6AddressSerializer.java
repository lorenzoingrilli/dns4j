/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.util;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer;

public class Inet6AddressSerializer implements ScalarSerializer<Inet6Address> {
	@Override
	public Inet6Address read(String ip) throws YamlException {
		try {
			return (Inet6Address) Inet6Address.getByName(ip);
		} catch (UnknownHostException e) {
			throw new YamlException(e);
		}
	}

	@Override
	public String write(Inet6Address ip) throws YamlException {
		return ip.getHostAddress();
	}
}