/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j;

import it.lorenzoingrilli.dns4j.daemon.util.Inet4AddressSerializer;
import it.lorenzoingrilli.dns4j.daemon.util.Inet6AddressSerializer;
import it.lorenzoingrilli.dns4j.daemon.util.InetAddressSerializer;
import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.resolver.NetEventListener;
import it.lorenzoingrilli.dns4j.resolver.impl.DNSClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;

/**
 * DNS command line client
 * 
 * @author Lorenzo Ingrilli'
 */
public class nsclient {

	private static final String RESOLV_CONF = File.separator+"etc"+File.separator+"resolv.conf";
	private static final String SEPARATOR = " ";
	private static final String COMMENT = " ";
	
	private static Options options = null;
	
    static {
    	OptionGroup g1 = new OptionGroup();
    	g1.setRequired(true);
    	g1.addOption(new Option("n", "name", true, "Name to query (mandatory)"));

    	OptionGroup g2 = new OptionGroup();
    	g2.setRequired(true);
    	g2.addOption(new Option("y", "yaml", false, "output in yaml format"));
    	g2.addOption(new Option("h", "human", false, "output in human format"));
    	g2.addOption(new Option("b", "base64", false, "output in base64 format"));
    	g2.addOption(new Option("H", "hex", false, "output in hexadecimal format"));    	

    	options = new Options();
    	options.addOptionGroup(g1);
    	options.addOptionGroup(g2);
    	options.addOption(new Option("v", "verbose", false, "Verbose log level"));
    	options.addOption(new Option("s", "server", true, "Nameserver host"));    	
    	options.addOption(new Option("N", "no-recursion", false, "No recursion desidered"));
    	options.addOption(new Option("t", "type", true, "query type (default A)"));
    	options.addOption(new Option("c", "class", true, "query class (default INET)"));
    	options.addOption(new Option("a", "attempts", true, "Number of times to try UDP queries to server (default 3)"));
    	options.addOption(new Option("T", "timeout", true, "timeout (in milliseconds) for a query (default 5 second)"));
    	options.addOption(new Option("X", "no-udp", false, "disable UDP requests"));
    	options.addOption(new Option("F", "no-tcp", false, "disable TCP requests"));
    }
    
    public static void main(String[] args) throws Exception {

    	CommandLine cmdline = parseCmdLine(options, args);

    	DNSClient client = new DNSClient();

    	// question detail 
    	String name = cmdline.getOptionValue('n');
    	int type = Integer.parseInt(cmdline.getOptionValue('t', Type.A+""));
    	int clazz = Integer.parseInt(cmdline.getOptionValue('c', Clazz.IN+""));
    	
       	// read settings from /etc/resolv.conf (if file exists)
       	configureResolvConf(client);

    	// nameserver list (if specified)
    	if(cmdline.hasOption('s')) {
    		List<InetSocketAddress> list = new LinkedList<InetSocketAddress>();    		
    		String serverString = cmdline.getOptionValue('s');
    		String servers[] = serverString.split(",");
    		for(String s: servers) {
    			String fields[] = s.split("\\@"); 
    			InetAddress addr = InetAddress.getByName(fields[0]);
    			int port = DNSClient.DEFAULT_PORT;
    			if(fields.length>1) {
    				port = Integer.parseInt(fields[1]);
    			}
    			list.add(new InetSocketAddress(addr, port));
    		}
        	client.setServers(list);
    	}
    	// if no name server was specificed (nor in /etc/resolv.conf nor in '-s' option) use localhost
    	if(client.getServers().size()==0) {
    		client.addServer(InetAddress.getByName(DNSClient.DEFAULT_SERVER), DNSClient.DEFAULT_PORT);
    	}
    	
    	// recursion desidered flag
    	boolean recursion = !cmdline.hasOption('N');

    	// protocol status (tcp/udp enabled/disabled)
       	client.setTcpEnabled(!cmdline.hasOption("no-tcp"));
       	client.setUdpEnabled(!cmdline.hasOption("no-udp"));

    	int timeout = Integer.parseInt(cmdline.getOptionValue('T', DNSClient.DEFAULT_TIMEOUT.toString()));
    	int numAttempts = Integer.parseInt(cmdline.getOptionValue('a', DNSClient.DEFAULT_NUM_ATTEMPTS.toString()));
    	
       	client.setTimeout(timeout);
       	client.setNumAttempts(numAttempts);
       	
        boolean human = cmdline.hasOption("human");
        boolean base64 = cmdline.hasOption("base64");
        boolean yaml = cmdline.hasOption("yaml");
        boolean hex = cmdline.hasOption("hex");
        NetEventListener nel = new OutputNetEventListener(hex, base64);
        client.setNetEventListener(nel);

       	MessageBuilder mb = new MessageBuilder();
       	
    	Message req = 
    		mb   		
    		.question()
    		.recursionDesidered(recursion)
    		.addQuestion(name, type, clazz)
    		.message();
    	
        Message resp = client.query(req);
    	        
        System.out.println("REQUEST:"); 
        output(req, human, base64, yaml, hex);
        System.out.println("RESPONSE:");
        output(resp, human, base64, yaml, hex);
        
        //System.out.println(client.getHostByName(name));
    }
    
    private static CommandLine parseCmdLine(Options options, String args[])
    {   	    	 
        try {
            CommandLineParser parser = new GnuParser();
            return parser.parse( options, args );
        }
        catch(ParseException exp ) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("nsclient", "Name Server Client", options, "Developed by Lorenzo Ingrilli' - http://www.lorenzoingrilli.it", true);
            System.exit(1);
        }
        return null;
    }
    
    private static void output(Message msg, boolean human, boolean hex, boolean yaml, boolean base64) throws YamlException {
    	if(human)
    		System.out.println(msg);
    	if(yaml) {
    		StringWriter sw = new StringWriter();
    		YamlWriter writer = new YamlWriter(sw);
    		YamlConfig config = writer.getConfig();
    		config.setScalarSerializer(InetAddress.class, new InetAddressSerializer());
    		config.setScalarSerializer(Inet4Address.class, new Inet4AddressSerializer());
    		config.setScalarSerializer(Inet6Address.class, new Inet6AddressSerializer());
    		writer.write(msg);  
    		writer.close();
        	System.out.println(sw);
    	}
    }
    
    private static void configureResolvConf(DNSClient client) throws IOException {
    	File f = new File(RESOLV_CONF);
    	if(f.exists() && f.canRead()) {
    		String domain = null;
    		List<String> nameservers = new LinkedList<String>();
    		List<String> searchList = new LinkedList<String>();
    		List<String> sortList = new LinkedList<String>();
    		
    		BufferedReader reader = new BufferedReader(new FileReader(f));
    		String line = null;
    		while( (line=reader.readLine()) != null) {
    			
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
    				String opt = fields[1].trim();
    				if(opt.startsWith("ndots:")) {
    					int ndots = Integer.parseInt(opt.split("\\:")[1]);
    					client.setNdots(ndots);
    				}
    			}
    		}
    		reader.close();
    		for(String server: nameservers) {
    			client.addServer(InetAddress.getByName(server), DNSClient.DEFAULT_PORT);
    		}
    		client.setDomain(domain);
    	}
    	
    }
    
}

class OutputNetEventListener implements NetEventListener {
	
	private boolean hex;
	private boolean base64;
	
	public OutputNetEventListener(boolean hex, boolean base64) {
		super();
		this.hex = hex;
		this.base64 = base64;
	}

	private byte[] copy(byte[] buffer, int offset, int len) {
		byte[] b = new byte[len];
		System.arraycopy(buffer, offset, b, 0, len);
		return b;
	}
	
	@Override
	public void onSent(byte[] buffer, int offset, int len) {
		if(base64) {
			String o = new String(Base64.encodeBase64(copy(buffer, offset, len)));
			System.out.println("SENT B64: "+o);
		}
		if(hex) {
			String o = Hex.encodeHexString(copy(buffer, offset, len)).toUpperCase();
			System.out.println("SENT HEX: "+o);
		}		
	}

	@Override
	public void onRecv(byte[] buffer, int offset, int len) {
		if(base64) {
			String o = new String(Base64.encodeBase64(copy(buffer, offset, len)));
			System.out.println("RECV B64: "+o);
		}
		if(hex) {
			String o = Hex.encodeHexString(copy(buffer, offset, len)).toUpperCase();
			System.out.println("RECV HEX: "+o);
		}		
	}
	
}
