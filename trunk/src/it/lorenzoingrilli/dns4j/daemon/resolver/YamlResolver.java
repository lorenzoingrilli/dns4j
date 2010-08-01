package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.daemon.util.Inet4AddressSerializer;
import it.lorenzoingrilli.dns4j.daemon.util.Inet6AddressSerializer;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AAAAImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.CNameImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.HInfoImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.MxImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.NsImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.PtrImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.RRImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SoaImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.TxtImpl;

import java.beans.ConstructorProperties;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class YamlResolver extends AuthoritativeResolver {
		
	public static final String DEFAULT_CONF = File.separator+"etc"+File.separator+"dns4j"+File.separator+"db.yml";
	
	public static final int DEFAULT_TTL = 86400;
    public static final int DEFAULT_SOA_SERIAL = 1;
    public static final int DEFAULT_SOA_REFRESH = 3600;
    public static final int DEFAULT_SOA_RETRY = 600;
    public static final int DEFAULT_SOA_EXPIRE = 86400;
    public static final int DEFAULT_SOA_MINIMUM = 3600;

	private String file = DEFAULT_CONF;

	private HashMap<ZoneEntryKey, List<RR>> map = new HashMap<ZoneEntryKey, List<RR>>();
	
	@ConstructorProperties(value={"file"})
	public YamlResolver(String file) throws IOException {
		this.file = file;
		parse();
	}
	
	public void parse() throws IOException {		
		YamlReader reader = new YamlReader(new FileReader(file));
		YamlConfig config = reader.getConfig();
		config.setClassTag("zone", Zone.class);
		config.setClassTag("rr", RRImpl.class);
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
					for(RR rr: e.getValue()) {
						if(rr.getTtl()==RRImpl.NONE)
							rr.setTtl(DEFAULT_TTL);
					}
				}
		}	
	}
	
	@Override
	public QuestionResponse query(String qname, int qclass, int qtype) {
		QuestionResponse qr = null;
		ZoneEntryKey k = new ZoneEntryKey(qname.toLowerCase(), qclass, qtype);
		List<RR> r = map.get(k);
		if(r!=null)
			for(RR rr: r) {
				if(qr==null)
					qr = new QuestionResponse();
				qr.getAnswer().add(rr);
			}
		return qr;
	}
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
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

