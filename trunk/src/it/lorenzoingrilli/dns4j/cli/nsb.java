package it.lorenzoingrilli.dns4j.cli;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.resolver.impl.DNSClient;

import java.net.InetAddress;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * NameServer Benchmark utility
 *
 * @author Lorenzo Ingrilli'
 */
public class nsb {
    
	private static final String DEFAULT_SERVER = "localhost";
	private static final String DEFAULT_PORT = "53";

	private static Options options = null;
	
    static {
    	options = new Options();
    	options.addOption(new Option("s", "server", true, "Nameserver host (default localhost)"));
    	options.addOption(new Option("p", "port", true, "Nameserver port (default 53)"));

    }

	public static void main(String[] args) throws Exception {

    	CommandLine cmdline = parseCmdLine(options, args);
    	
    	DNSClient client = new DNSClient();
    	
    	// if no name server was specificed (nor in /etc/resolv.conf nor in '-s' option) use localhost
    	if(client.getServers().size()==0) {
    		
    	}    	

    	int port = Integer.parseInt(cmdline.getOptionValue('p', DEFAULT_PORT));
    	String host = cmdline.getOptionValue('s', DEFAULT_SERVER).toLowerCase();
    	client.addServer(InetAddress.getByName(host), port);
    	
       	client.setTimeout(10000);
       	client.setNumAttempts(1);

       	MessageBuilder mb = new MessageBuilder();
       	
    	mb   		
    		.question()
    		.addQuestion("www.example.net", Type.A, Clazz.IN)
    		.recursionDesidered(true)
    		.message();
    	
    	long ts1 = System.currentTimeMillis();
    	
    	for(int i=0; i<5000; i++) {
    		Message req = mb.randomId().message();
    		Message resp = client.query(req);
    	}
        
        long ts2 = System.currentTimeMillis();
    	
        System.out.println("TIME "+(ts2-ts1)); 		    	    	
    }
    
    private static CommandLine parseCmdLine(Options options, String args[])
    {   	    	 
        try {
            CommandLineParser parser = new GnuParser();
            return parser.parse( options, args );
        }
        catch(ParseException exp ) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("nsc", "Name Server Benchmark", options, "Developed by Lorenzo Ingrilli' - http://www.lorenzoingrilli.it", true);
            System.exit(1);
        }
        return null;
    }
    
}

