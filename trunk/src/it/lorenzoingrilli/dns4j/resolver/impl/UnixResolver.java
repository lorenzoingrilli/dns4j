package it.lorenzoingrilli.dns4j.resolver.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class UnixResolver {
	
	public static final String RESOLV_CONF = File.pathSeparator+"etc"+File.pathSeparator+"resolv.conf";
	public static final String SEPARATOR = " ";
	public static final String COMMENT = " ";
	
	private String resolvConf;
	private List<String> nameservers;
	private String domain;
	private List<String> searchList;
	private List<String> sortList;
	private List<String> options;
	
	
	public UnixResolver() throws IOException {
		this(RESOLV_CONF);
	}
	
	public UnixResolver(String resolvConf) throws IOException {
		this.resolvConf = resolvConf;
		parse();
	}
	
	public void parse() throws IOException {		
		nameservers = new LinkedList<String>();
		domain = null;
		searchList = new LinkedList<String>();
		sortList = new LinkedList<String>();
		options = new LinkedList<String>();
		
		BufferedReader r = new BufferedReader(new FileReader(resolvConf));
		String line = null;
		while( (line=r.readLine()) != null) {
			
			line = line.trim();
			if(line.startsWith(COMMENT))
				continue;
			
			String fields[] = line.split(SEPARATOR);
			if("nameserver".equals(fields[0])) {
				nameservers.add(fields[1]);
			}
			else if("domain".equals(fields[0])) {
				domain = fields[1];
			}
			else if("search".equals(fields[0])) {
				searchList.add(fields[1]);
			}
			else if("sortList".equals(fields[0])) {
				sortList.add(fields[1]);
			}
			else if("options".equals(fields[0])) {
				options.add(fields[1]);
			}
		}
		r.close();
	}
	
	public String getResolvConf() {
		return resolvConf;
	}

	public void setResolvConf(String resolvConf) {
		this.resolvConf = resolvConf;
	}
	
	public List<String> getNameservers() {
		return nameservers;
	}

	public String getDomain() {
		return domain;
	}

	public List<String> getSortList() {
		return sortList;
	}

	public List<String> getOptions() {
		return options;
	}

	public List<String> getSearchList() {
		return searchList;
	}

}

