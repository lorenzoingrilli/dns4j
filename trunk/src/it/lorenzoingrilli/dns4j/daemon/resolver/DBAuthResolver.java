package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AImpl;

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
	private String a = "SELECT id,ttl,address FROM a WHERE name=?";	
		
	@Override
	public QuestionResponse query(String qname, int qclass, int qtype) {
		try {
		QuestionResponse qr = null;
		List<Map<String, Object>> list = null;
		switch (qtype) {
		case Type.A:
			list = sqlQuery(a, qname);
			if(list.size()>0) {
				qr = new QuestionResponse();
				for(Map<String, Object> r: list) {
					AImpl rr = new AImpl();
					rr.setTtl((Integer) r.get("ttl"));
					rr.setAddress((Inet4Address) InetAddress.getByName((String) r.get("address")));
					rr.setName(qname);
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
	
}
