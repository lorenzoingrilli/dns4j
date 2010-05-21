package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AAAAImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.CNameImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.HInfoImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.MxImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.NsImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.PtrImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SoaImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.TxtImpl;

import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class YamlResolver extends AuthoritativeResolver {
		
	private String file;
	private HashMap<ZoneEntryKey, List<RR>> map = new HashMap<ZoneEntryKey, List<RR>>();

	public YamlResolver(String file) throws IOException {
		this.file = file;
		parse();
	}
	
	public void parse() throws IOException {		
		YamlReader reader = new YamlReader(new FileReader(file));
		YamlConfig config = reader.getConfig();
		config.setClassTag("zone", Zone.class);
		config.setClassTag("soa", SoaImpl.class);
		config.setClassTag("a", AImpl.class);
		config.setClassTag("aaaa", AAAAImpl.class);		
		config.setClassTag("cname", CNameImpl.class);
		config.setClassTag("hinfo", HInfoImpl.class);
		config.setClassTag("ns", NsImpl.class);
		config.setClassTag("mx", MxImpl.class);
		config.setClassTag("ptr", PtrImpl.class);
		config.setClassTag("txt", TxtImpl.class);		
		config.setScalarSerializer(Inet4Address.class, new Inet4AddressSerializer());
		config.setScalarSerializer(Inet6Address.class, new Inet6AddressSerializer());
		
		Zone zone = null;
		while( (zone = (Zone) reader.read()) != null) {
				Set<Entry<ZoneEntryKey, List<RR>>> es = zone.getMap().entrySet();				
				for(Entry<ZoneEntryKey, List<RR>> e: es) {
					map.put(e.getKey(), e.getValue());
				}
		}	
	}
	
	@Override
	public QuestionResponse query(Question q) {
		QuestionResponse qr = null;
		String name = q.getQname();
		//int n = name.indexOf('.');
		//String hostname = name.substring(0, n);
		//String zonename = name.substring(n+1);
		ZoneEntryKey k = new ZoneEntryKey(name.toLowerCase(), q.getQclass(), q.getQtype());
		List<RR> r = map.get(k);
		if(r!=null)
			for(RR rr: r) {
				if(qr==null)
					qr = new QuestionResponse();
				qr.getAnswer().add(rr);
			}
		return qr;
	}

}

class ZoneEntryKey {
	private String name;
	private int qclass;
	private int qtype;
	
	public ZoneEntryKey(String name, int qclass, int qtype) {
		super();
		this.name = name;
		this.qclass = qclass;
		this.qtype = qtype;
	}
	
	@Override
	public String toString() {
		return "zKey(name="+name+", class="+qclass+", type="+qtype+")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
		result = prime * result + qclass;
		result = prime * result + qtype;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoneEntryKey other = (ZoneEntryKey) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		if (qclass != other.qclass)
			return false;
		if (qtype != other.qtype)
			return false;
		return true;
	}
	
	public String getName() {
		return name;
	}

	public int getQclass() {
		return qclass;
	}

	public int getQtype() {
		return qtype;
	}	
	
}

class Inet4AddressSerializer implements ScalarSerializer<Inet4Address> {
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

class Inet6AddressSerializer implements ScalarSerializer<Inet6Address> {
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
