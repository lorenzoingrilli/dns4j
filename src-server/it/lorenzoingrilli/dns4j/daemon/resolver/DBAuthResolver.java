/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.protocol.Type;
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
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * @author Lorenzo Ingrilli'
 */
public class DBAuthResolver extends AuthoritativeResolver {
			
	private static final String TTL		= "ttl";
	private static final String HOST	= "host";
	private static final String ADDRESS	= "address";
	private static final String DATA	= "data";	
	private static final String TYPE	= "type";
	private static final String CLAZZ	= "class";
	
	private DataSource dataSource;

	private boolean handleUnknown = false;
	
	private String sqlQuerySoa		= "SELECT ttl, serial, refresh, expire, retry, minimum, email FROM soa WHERE name=?";
	private String sqlQueryA		= "SELECT ttl, address FROM a WHERE name=?";
	private String sqlQueryAaaa		= "SELECT ttl, address FROM aaaa WHERE name=?";
	private String sqlQueryCname	= "SELECT ttl, host FROM cname WHERE name=?";
	private String sqlQueryNs		= "SELECT ttl, host FROM ns WHERE name=?";
	private String sqlQueryMx 		= "SELECT ttl, host, priority FROM mx WHERE name=?";
	private String sqlQueryTxt		= "SELECT ttl, data WHERE name=?";
	private String sqlQueryPtr 		= "SELECT ttl, address FROM ptr WHERE name=?";		
	private String sqlQueryHinfo	= "SELECT ttl, host, cpu FROM hinfo WHERE name=?";
	private String sqlQueryRr		= "SELECT ttl, data FROM rr WHERE name=? AND class=? AND type=?";
	
	/*
	private String sqlQuerySoa		= "SELECT dns_zone.name, dns_zone.email AS email, dns_zone.ttl, 0 as serial, dns_zone.refresh, dns_zone.retry, dns_zone.expire, dns_zone.minimum FROM dns_zone WHERE dns_zone.name=?";
	private String sqlQueryA		= "SELECT dns_recs.val as address, dns_zone.ttl from dns_zone inner join dns_recs on dns_zone.id=dns_recs.dns_zone_id where TRIM(TRAILING '.' FROM dns_recs.host)=? and dns_recs.type='A'";
	private String sqlQueryNs		= "SELECT TRIM(TRAILING '.' FROM dns_recs.val) AS host, dns_zone.ttl from dns_zone inner join dns_recs on dns_zone.id=dns_recs.dns_zone_id where TRIM(TRAILING '.' FROM dns_recs.host)=? and dns_recs.type='NS'";	
	private String sqlQueryCname	= "SELECT TRIM(TRAILING '.' FROM dns_recs.val) AS host, dns_zone.ttl from dns_zone inner join dns_recs on dns_zone.id=dns_recs.dns_zone_id where TRIM(TRAILING '.' FROM dns_recs.host)=? and dns_recs.type='CNAME'";	
	private String sqlQueryPtr 		= "SELECT TRIM(TRAILING '.' FROM dns_recs.val) AS host, dns_zone.ttl, dns_recs.type from dns_zone inner join dns_recs on dns_zone.id=dns_recs.dns_zone_id where TRIM(TRAILING '.' FROM dns_recs.host)=? and dns_recs.type='PTR'";
	private String sqlQueryMx 		= "SELECT TRIM(TRAILING '.' FROM dns_recs.val) AS host, CAST(opt AS SIGNED) AS preference, dns_zone.ttl from dns_zone inner join dns_recs on dns_zone.id=dns_recs.dns_zone_id where TRIM(TRAILING '.' FROM dns_recs.host)=? and dns_recs.type='MX'";
	*/
				
	@ConstructorProperties(value={"dataSource"})
	public DBAuthResolver(DataSource ds) {
		setDataSource(ds);
	}
	
	@Override
	public Collection<RR> query(String qname, int qclass, int qtype) {
		try {
		LinkedList<RR> rrs = new LinkedList<RR>();		
		List<Map<String, Object>> list = null;
		switch (qtype) {
		case Type.SOA:
			list = sqlQuery(sqlQuerySoa, qname);
			for(Map<String, Object> r: list) {
				SoaImpl rr = 
					new SoaImpl(
							qname,
							(Long) r.get(TTL),
							qname,
							((String) r.get("email")).replace('@', '.'), 
							(Long) r.get("serial"), 
							(Long) r.get("refresh"),
							(Long) r.get("retry"),
							(Long) r.get("expire"),
							(Long) r.get("minimum")
					);					
				rrs.add(rr);
			}
			break;			
		case Type.A:
			list = sqlQuery(sqlQueryA, qname);
			for(Map<String, Object> r: list) {					
				AImpl rr = new AImpl(
						qname, 
						(Long) r.get(TTL),
						(Inet4Address) InetAddress.getByName((String) r.get(ADDRESS))							
				);
				rrs.add(rr);
			}
			break;
		case Type.AAAA:
			list = sqlQuery(sqlQueryAaaa, qname);
			for(Map<String, Object> r: list) {
				AAAAImpl rr = new AAAAImpl(					
						qname, 
						(Long) r.get(TTL),
						(Inet6Address) InetAddress.getByName((String) r.get(ADDRESS))							
				);
				rrs.add(rr);
			}
			break;
		case Type.CNAME:
			list = sqlQuery(sqlQueryCname, qname);
			for(Map<String, Object> r: list) {
				CNameImpl rr = new CNameImpl(
						qname, 
						(Long) r.get(TTL),
						(String) r.get(HOST)							
				);
				rrs.add(rr);
			}
			break;
		case Type.NS:
			list = sqlQuery(sqlQueryNs, qname);
			for(Map<String, Object> r: list) {
				NsImpl rr = new NsImpl(
						qname, 
						(Long) r.get(TTL),
						(String) r.get(HOST)							
				);
				rrs.add(rr);
			}
			break;
		case Type.MX:
			list = sqlQuery(sqlQueryMx, qname);
			for(Map<String, Object> r: list) {
				MxImpl rr = new MxImpl(
						qname,
						(Long) r.get(TTL),
						(String) r.get(HOST),
						(int)(long)(Long) r.get("priority")
				);
				rrs.add(rr);
			}
			break;
		case Type.TXT:
			list = sqlQuery(sqlQueryTxt, qname);
			for(Map<String, Object> r: list) {
				TxtImpl rr = new TxtImpl(
						qname,
						(Long) r.get(TTL),
						(String) r.get(DATA)
				);
				rrs.add(rr);
			}
			break;
		case Type.HINFO:
			list = sqlQuery(sqlQueryHinfo, qname);
			for(Map<String, Object> r: list) {
				HInfoImpl rr = new HInfoImpl(
						qname,
						(Long) r.get(TTL),
						(String) r.get("host"),
						(String) r.get("cpu")
				);
				rrs.add(rr);
			}
			break;
		case Type.PTR:
			InetAddress addr = PtrImpl.nameToAddress(qname);
			list = sqlQuery(sqlQueryPtr, addr.getHostAddress());
			for(Map<String, Object> r: list) {
				PtrImpl rr = new PtrImpl(
						qname, 
						(Long) r.get(TTL),
						(String) r.get(HOST)							
				);
				rrs.add(rr);
			}
			break;
		default:
			if(handleUnknown) {
				list = sqlQuery(sqlQueryRr, qname, qclass, qtype);
				for(Map<String, Object> r: list) {
					RRImpl rr = new RRImpl(
							qname,
							(Integer) r.get(TYPE),
							(Integer) r.get(CLAZZ),
							(Long) r.get(TTL),
							(byte[]) r.get(DATA)
						);
					rrs.add(rr);
				}
			}
			break;
		}
		return rrs;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		
	protected List<Map<String, Object>> sqlQuery(String sql, Object ...params) throws SQLException {
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		if(sql==null) {
			return list;
		}
		
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(true);
			connection.setReadOnly(true);
			PreparedStatement stmt = connection.prepareStatement(sql);
			for(int i=1; i<=params.length; i++) {
				stmt.setObject(i, params[i-1]);
			}			
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData md = rs.getMetaData();

			int nCols = md.getColumnCount();
			while(rs.next()) {
				HashMap<String, Object> result = new HashMap<String, Object>();
				for(int i=1; i<=nCols; i++) {
					String name = md.getColumnLabel(i);
					Object val = rs.getObject(i);
					result.put(name, val);
				}
				list.add(result);
			}
			rs.close();
			stmt.close();
		}
		finally {
			if(connection!=null)
				connection.close();
		}
		return list;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public boolean isHandleUnknown() {
		return handleUnknown;
	}

	public void setHandleUnknown(boolean handleUnknown) {
		this.handleUnknown = handleUnknown;
	}

	public String getSqlQuerySoa() {
		return sqlQuerySoa;
	}

	public void setSqlQuerySoa(String sqlQuerySoa) {
		this.sqlQuerySoa = sqlQuerySoa;
	}

	public String getSqlQueryA() {
		return sqlQueryA;
	}

	public void setSqlQueryA(String sqlQueryA) {
		this.sqlQueryA = sqlQueryA;
	}

	public String getSqlQueryCname() {
		return sqlQueryCname;
	}

	public void setSqlQueryCname(String sqlQueryCname) {
		this.sqlQueryCname = sqlQueryCname;
	}

	public String getSqlQueryPtr() {
		return sqlQueryPtr;
	}

	public void setSqlQueryPtr(String sqlQueryPtr) {
		this.sqlQueryPtr = sqlQueryPtr;
	}

	public String getSqlQueryNs() {
		return sqlQueryNs;
	}

	public void setSqlQueryNs(String sqlQueryNs) {
		this.sqlQueryNs = sqlQueryNs;
	}

	public String getSqlQueryMx() {
		return sqlQueryMx;
	}

	public void setSqlQueryMx(String sqlQueryMx) {
		this.sqlQueryMx = sqlQueryMx;
	}

	public String getSqlQueryRr() {
		return sqlQueryRr;
	}

	public void setSqlQueryRr(String sqlQueryRr) {
		this.sqlQueryRr = sqlQueryRr;
	}
	
}
