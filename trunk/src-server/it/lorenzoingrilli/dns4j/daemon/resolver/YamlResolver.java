/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.daemon.Plugin;
import it.lorenzoingrilli.dns4j.daemon.Kernel;
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
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SrvImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.TxtImpl;

import java.beans.ConstructorProperties;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * @author Lorenzo Ingrilli'
 */
public class YamlResolver extends AuthoritativeResolver implements Plugin, Runnable {
		
	private static Logger logger = Logger.getLogger(YamlResolver.class.getName());
	
	private static final long DEFAULT_LIVE_REFRESH = 5000; 
		
	private File file = null;
	private long liveRefresh = DEFAULT_LIVE_REFRESH;
	private long lastModified = 0;

	private HashMap<ZoneEntryKey, List<RR>> map;
	
	@ConstructorProperties(value={"file"})
	public YamlResolver(File file) throws IOException {
		setFile(file);
		map = parse(file);
	}
	
	private HashMap<ZoneEntryKey, List<RR>> parse(File file) throws IOException {		
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
		config.setClassTag("srv", SrvImpl.class);	
		config.setScalarSerializer(Inet4Address.class, new Inet4AddressSerializer());
		config.setScalarSerializer(Inet6Address.class, new Inet6AddressSerializer());
		
		HashMap<ZoneEntryKey, List<RR>> map = new HashMap<ZoneEntryKey, List<RR>>();
		Zone zone = null;
		while( (zone = (Zone) reader.read()) != null) {
				Set<Entry<ZoneEntryKey, List<RR>>> es = zone.getMap().entrySet();				
				for(Entry<ZoneEntryKey, List<RR>> e: es) {
					map.put(e.getKey(), e.getValue());
					for(RR rr: e.getValue()) {
						if(rr.getTtl()==RRImpl.NONE)							
							rr.setTtl(getDefaultTtl());
					}
				}
		}
		return map;
	}
	
	@Override
	public Collection<RR> query(String qname, int qclass, int qtype) {
		ZoneEntryKey k = new ZoneEntryKey(qname.toLowerCase(), qclass, qtype);
		List<RR> r = map.get(k);
		if(r!=null) 
			return r;
		else
			return new LinkedList<RR>();
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted())
		try {
			if(liveRefresh<0) {
				Thread.sleep(DEFAULT_LIVE_REFRESH);
				continue;
			}
			Thread.sleep(liveRefresh);
			long ts = file.lastModified(); 
			if(ts>lastModified) {
				map = parse(file);
				lastModified = ts;
				logger.log(Level.INFO, "Yaml Zone file reloaded");
			}
		} catch (InterruptedException e) {
			return;
		} catch (IOException e) {
			logger.log(Level.WARNING, "Broken yaml configuration", e);
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		lastModified = file.lastModified();		
	}
	
	public void setFilename(String filename) {
		setFile(new File(filename));
	}
	
	public String getFilename() {
		return file.getPath();
	}
	
	public long getLiveRefresh() {
		return liveRefresh;
	}

	public void setLiveRefresh(long liveRefresh) {
		this.liveRefresh = liveRefresh;
	}

	@Override
	public void init(Kernel kernel) {	
	}

	@Override
	public void destroy() {
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

