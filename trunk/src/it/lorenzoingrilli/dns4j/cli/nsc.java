package it.lorenzoingrilli.dns4j.cli;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.resolver.impl.DNSClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * DNS command line client
 * 
 * @author Lorenzo Ingrilli'
 */
public class nsc {

	private static final String DEFAULT_SERVER = "localhost";
	private static final String DEFAULT_PORT = "53";
	private static final String DEFAULT_TIMEOUT = "5000";
	private static final String DEFAULT_ATTEMPTS = "3";
	private static final String DEFAULT_OUTPUT = "human";
	private static final String RESOLV_CONF = File.separator+"etc"+File.separator+"resolv.conf";
	private static final String SEPARATOR = " ";
	private static final String COMMENT = " ";
	
	private static Options options = null;
	
    static {
    	options = new Options();
    	options.addOption(new Option("v", "verbose", false, "Verbose log level"));
    	options.addOption(new Option("s", "server", true, "Nameserver host (default localhost)"));
    	options.addOption(new Option("p", "port", true, "Nameserver port (default 53)"));
    	options.addOption(new Option("n", "name", true, "Name to query (mandatory)"));
    	options.addOption(new Option("N", "no-recursion", false, "No recursion desidered"));
    	options.addOption(new Option("t", "type", true, "query type (default A)"));
    	options.addOption(new Option("c", "class", true, "query class (default INET)"));
    	options.addOption(new Option("a", "attempts", true, "Number of times to try UDP queries to server (default 3)"));
    	options.addOption(new Option("T", "timeout", true, "timeout (in milliseconds) for a query (default 5 second)"));
    	options.addOption(new Option("o", "output", true, "tipo di output: human,yaml,b64,hex"));
    	options.addOption(new Option("H", "no-udp", false, "disable UDP requests"));
    	options.addOption(new Option("F", "no-tcp", false, "disable TCP requests"));
    }
    
    public static void main(String[] args) throws Exception {

    	CommandLine cmdline = parseCmdLine(options, args);
    	
    	DNSClient client = new DNSClient();

    	// question detail 
    	String name = cmdline.getOptionValue('n');
    	int type = Integer.parseInt(cmdline.getOptionValue('t', Type.A+""));
    	int clazz = Integer.parseInt(cmdline.getOptionValue('c', Clazz.IN+""));

    	// main nameserver (if specified)
    	if(cmdline.hasOption('s')) {
        	InetAddress address = InetAddress.getByName(cmdline.getOptionValue('s'));
        	int port = Integer.parseInt(cmdline.getOptionValue('p', DEFAULT_PORT));
        	client.addServer(address, port);
    	}
    	
    	// recursion desidered flag
    	boolean recursion = !cmdline.hasOption('N');

    	// protocol status (tcp/udp enabled/disabled)
       	client.setTcpEnabled(!cmdline.hasOption("no-tcp"));
       	client.setUdpEnabled(!cmdline.hasOption("no-udp"));

    	int timeout = Integer.parseInt(cmdline.getOptionValue('T', DEFAULT_TIMEOUT));
    	int numAttempts = Integer.parseInt(cmdline.getOptionValue('a', DEFAULT_ATTEMPTS));
    	String output = cmdline.getOptionValue('o', DEFAULT_OUTPUT).toLowerCase();
    	
       	client.setTimeout(timeout);
       	client.setNumAttempts(numAttempts);
       	
       	// read settings from /etc/resolv.conf (if file exists)
       	configureResolvConf(client);

    	Message req = 
    		new MessageBuilder()
    		.question()
    		.recursionDesidered(recursion)
    		.addQuestion(name, type, clazz)
    		.message();
    	
        Message resp = client.query(req);
    	
    	if("human".equals(output)) {
        	System.out.println("REQUEST  "+req);
        	System.out.println("RESPONSE "+resp);    		
    	}
    	else if("b64".equals(output)) {
        	System.out.println("REQUEST  "+req);
        	System.out.println("RESPONSE "+resp);    		
    	}
    	else if("hex".equals(output)) {
        	System.out.println("REQUEST  "+req);
        	System.out.println("RESPONSE "+resp);    		
    	}
    	else if("yaml".equals(output)) {
        	System.out.println("REQUEST  "+req);
        	System.out.println("RESPONSE "+resp);    		
    	} 
    	    	
    }
    
    private static CommandLine parseCmdLine(Options options, String args[])
    {   	    	 
        try {
            CommandLineParser parser = new GnuParser();
            return parser.parse( options, args );
        }
        catch(ParseException exp ) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("nsc", "Name Server Client", options, "Developed by Lorenzo Ingrilli' - http://www.lorenzoingrilli.it", true);
            System.exit(1);
        }
        return null;
    }
    
    private static void configureResolvConf(DNSClient client) throws IOException {
    	File f = new File(RESOLV_CONF);
    	if(f.exists() && f.canRead()) {
    		String domain = null;
    		List<String> nameservers = new LinkedList<String>();
    		List<String> searchList = new LinkedList<String>();
    		List<String> sortList = new LinkedList<String>();
    		List<String> options = new LinkedList<String>();
    		
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
    				options.add(fields[1]);
    			}
    		}
    		reader.close();
    		for(String server: nameservers) {
    			client.addServer(InetAddress.getByName(server), 53);
    		}
    	}
    	
    }
    
}
