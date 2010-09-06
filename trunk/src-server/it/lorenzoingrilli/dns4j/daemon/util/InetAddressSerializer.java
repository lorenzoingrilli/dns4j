package it.lorenzoingrilli.dns4j.daemon.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer;

public class InetAddressSerializer implements ScalarSerializer<InetAddress> {
	@Override
	public InetAddress read(String ip) throws YamlException {
		try {
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			throw new YamlException(e);
		}
	}

	@Override
	public String write(InetAddress ip) throws YamlException {
		return ip.getHostAddress();
	}
}
