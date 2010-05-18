package it.lorenzoingrilli.dns4j.cli;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.protocol.impl.QuestionImpl;
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
 * DNS command line client
 * 
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class nsc {

	private static final String DEFAULT_SERVER = "localhost";
	private static final String DEFAULT_PORT = "53";
	private static final String DEFAULT_TIMEOUT = "5000";
	private static final String DEFAULT_ATTEMPTS = "3";
	
	private static Options options = null;
	
    static {
    	options = new Options();
    	options.addOption(new Option("v", "verbose", false, "Verbose log level"));
    	options.addOption(new Option("s", "server", true, "Nameserver host (default localhost)"));
    	options.addOption(new Option("p", "port", true, "Nameserver port (default 53)"));    	
    	options.addOption(new Option("n", "name", true, "Name to query (mandatory)"));
    	options.addOption(new Option("N", "no-recursion", false, "No recursion desidered"));
    	options.addOption(new Option("r", "recursion", false, "Recursion desidered (default)"));
    	options.addOption(new Option("t", "type", true, "query type (default A)"));
    	options.addOption(new Option("c", "class", true, "query class (default INET)"));
    	options.addOption(new Option("a", "attempts", true, "Number of times to try UDP queries to server (default 3)"));
    	options.addOption(new Option("T", "timeout", true, "timeout (in milliseconds) for a query (default 5 second)"));
    	options.addOption(new Option("f", "follow", false, "follow authority"));
    }
    
    public static void main(String[] args) throws Exception {

    	CommandLine cmdline = parseCmdLine(options, args);
    	
    	InetAddress server = InetAddress.getByName(cmdline.getOptionValue('s', DEFAULT_SERVER));
    	int port = Integer.parseInt(cmdline.getOptionValue('p', DEFAULT_PORT));
    	String name = cmdline.getOptionValue('n');    	
    	boolean recusion = !cmdline.hasOption('N') || cmdline.hasOption('r');
    	boolean follow = cmdline.hasOption('f');
    	int timeout = Integer.parseInt(cmdline.getOptionValue('T', DEFAULT_TIMEOUT));
    	int numAttempts = Integer.parseInt(cmdline.getOptionValue('a', DEFAULT_ATTEMPTS));
    	int type = Integer.parseInt(cmdline.getOptionValue('t', Type.A+""));
    	int clazz = Integer.parseInt(cmdline.getOptionValue('c', Clazz.IN+""));    	
    	    	
       	DNSClient client = new DNSClient(server, port);
       	client.setTimeout(timeout);
       	client.setNumAttempts(numAttempts);

    	Message req = 
    		new MessageBuilder()
    		.setRecursionDesidered(recusion)
    		.addQuestion(new QuestionImpl(name, type, clazz))
    		.message();
    	
        Message resp = client.query(req);
            	
    	System.out.println("REQUEST  "+req);
    	System.out.println("RESPONSE "+resp);
    	
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
    
}
