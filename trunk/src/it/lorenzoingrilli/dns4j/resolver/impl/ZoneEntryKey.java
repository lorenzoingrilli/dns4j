package it.lorenzoingrilli.dns4j.resolver.impl;

public class ZoneEntryKey {
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		} else if (!name.equals(other.name))
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
