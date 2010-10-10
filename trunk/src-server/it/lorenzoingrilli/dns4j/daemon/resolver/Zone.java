/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.resolver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import it.lorenzoingrilli.dns4j.protocol.rr.RR;

/**
 * @author Lorenzo Ingrilli'
 */
public class Zone {
	private String name;
	private List<RR> rrs;
	private HashMap<ZoneEntryKey, List<RR>> map;

	@Override
	public String toString() {
		return "Zone("+name+", rrs="+rrs+")"; 
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RR> getRrs() {
		return rrs;
	}
	
	public void setRrs(List<RR> rrList) {
		this.rrs = rrList;
		map = new HashMap<ZoneEntryKey, List<RR>>();
		for(RR rr: rrList) {
			ZoneEntryKey k = new ZoneEntryKey(rr.getName(), rr.getClazz(), rr.getType());
			List<RR> l = map.get(k);
			if(l==null) {
				l = new LinkedList<RR>();
				map.put(k, l);				
			}
			l.add(rr);
		}
	}
	
	public HashMap<ZoneEntryKey, List<RR>> getMap() {
		return map;
	}
		
}
