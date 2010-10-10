/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer;

public class Inet4AddressSerializer implements ScalarSerializer<Inet4Address> {
	@Override
	public Inet4Address read(String ip) throws YamlException {
		try {
			return (Inet4Address) Inet4Address.getByName(ip);
		} catch (UnknownHostException e) {
			throw new YamlException(e);
		}
	}

	@Override
	public String write(Inet4Address ip) throws YamlException {
		return ip.getHostAddress();
	}
}
