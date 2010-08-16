package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.CNameImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.MxImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.NsImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.PtrImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SoaImpl;

import java.beans.ConstructorProperties;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * @author Lorenzo Ingrilli'
 */
public class DBAuthResolver extends AuthoritativeResolver {
			
	private DataSource dataSource;

	private String sqlQuerySoa		= "SELECT ttl, serial, refresh, expire, retry, minimum, email FROM soa WHERE name=?";
	private String sqlQueryA		= "SELECT ttl, address FROM a WHERE name=?";
	private String sqlQueryNs		= "SELECT ttl, nsdname AS host FROM ns WHERE name=?";	
	private String sqlQueryCname	= "SELECT ttl, cname AS host FROM cname WHERE name=?";	
	private String sqlQueryPtr 		= "SELECT ttl, ptrdname AS host FROM ptr WHERE name=?";
	private String sqlQueryMx 		= "SELECT ttl, preference AS priority, exchange AS host FROM mx WHERE name=?";
	
	//private String sqlQueryAaaa = null;
	//private String sqlQueryTxt = null;
	//private String sqlQuerySrv = null;
	//private String sqlQueryHinfo = null;
	//private String sqlQueryRr = null;
				
	@ConstructorProperties(value={"dataSource"})
	public DBAuthResolver(DataSource ds) {
		setDataSource(ds);
	}
	
	@Override
	public QuestionResponse query(String qname, int qclass, int qtype) {
		try {
		QuestionResponse qr = null;
		List<Map<String, Object>> list = null;
		switch (qtype) {
		case Type.SOA:
			list = sqlQuery(sqlQuerySoa, qname);
			if(list.size()>0) {
				qr = new QuestionResponse();
				for(Map<String, Object> r: list) {
					SoaImpl rr = 
						new SoaImpl(
								qname,
								(Long) r.get("ttl"),
								qname,
								((String) r.get("email")).replace('@', '.'), 
								(Long) r.get("serial"), 
								(Long) r.get("refresh"),
								(Long) r.get("retry"),
								(Long) r.get("expire"),
								(Long) r.get("minimum")
								);					
					qr.getAnswer().add(rr);
				}
			}
			break;			
		case Type.A:
			list = sqlQuery(sqlQueryA, qname);
			if(list.size()>0) {
				qr = new QuestionResponse();
				for(Map<String, Object> r: list) {					
					AImpl rr = new AImpl(qname, (Inet4Address) InetAddress.getByName((String) r.get("address")), (Long) r.get("ttl"));
					qr.getAnswer().add(rr);
				}
			}
			break;
		case Type.NS:
			list = sqlQuery(sqlQueryNs, qname);
			if(list.size()>0) {
				qr = new QuestionResponse();
				for(Map<String, Object> r: list) {
					NsImpl rr = new NsImpl(qname, (String) r.get("host"), (Long) r.get("ttl"));
					qr.getAnswer().add(rr);
				}
			}
			break;
		case Type.CNAME:
			list = sqlQuery(sqlQueryCname, qname);
			if(list.size()>0) {
				qr = new QuestionResponse();
				for(Map<String, Object> r: list) {
					CNameImpl rr = new CNameImpl(qname, (String) r.get("host"), (Long) r.get("ttl"));
					qr.getAnswer().add(rr);
				}
			}
			break;
		case Type.PTR:
			InetAddress addr = PtrImpl.nameToAddress(qname);
			list = sqlQuery(sqlQueryPtr, addr.getHostAddress());
			if(list.size()>0) {
				qr = new QuestionResponse();
				for(Map<String, Object> r: list) {
					PtrImpl rr = new PtrImpl(qname, (String) r.get("host"), (Long) r.get("ttl"));
					qr.getAnswer().add(rr);
				}
			}
			break;
		case Type.MX:
			list = sqlQuery(sqlQueryMx, qname);
			if(list.size()>0) {
				qr = new QuestionResponse();
				for(Map<String, Object> r: list) {
					MxImpl rr = new MxImpl(qname, (Long) r.get("ttl"), (String) r.get("host"), (int)(long)(Long) r.get("priority"));
					qr.getAnswer().add(rr);
				}
			}
			break;
		default:
			break;
		}
		return qr;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		
	protected List<Map<String, Object>> sqlQuery(String sql, Object ...params) throws SQLException {
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
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
		catch(Exception e) {
			e.printStackTrace();
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
	
}
