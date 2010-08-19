package it.lorenzoingrilli.dns4j;

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
    	final int port = Integer.parseInt(cmdline.getOptionValue('p', DEFAULT_PORT));
    	final InetAddress host = InetAddress.getByName(cmdline.getOptionValue('s', DEFAULT_SERVER).toLowerCase());    	
    	
    	
    	// if no name server was specificed (nor in /etc/resolv.conf nor in '-s' option) use localhost
    	/*if(client.getServers().size()==0) {
    		
    	}   */ 	
    	
    	String names[] = { 
    			"www.google.it", 
    			"www.gentoo.org", 
    			"www.kde.org", 
    			"www.lorenzoingrilli.it",
    			"www.openbsd.org",
    			"www.freebsd.org",
    			"www.microsoft.com",
    			"www.kernel.org",
    			"www.ansa.it",
    			"www.punto-informatico.it"
    			};
    	
    	long ts1 = System.currentTimeMillis();
    	
    	int n=10;
    	Thread t[] = new Thread[n];
    	for(int i=0; i<t.length; i++) {
    		//Thread.sleep(3000);
    		final Message req =
    			new MessageBuilder()   		
    			.question()
    			.addQuestion(names[i], Type.A, Clazz.IN)
    			.recursionDesidered(true)
    			.message();
    		final int x = i;
    		t[i] = new Thread(new Runnable() {				
				@Override
				public void run() {
			    	DNSClient client = new DNSClient();
			    	client.addServer(host, port);    	
					client.setTimeout(5000);
			       	client.setNumAttempts(1);
					System.out.println("T["+x+"]: SENT "+req);
		    		Message resp = client.query(req);
		    		System.out.println("T["+x+"]: RECV "+resp);
				}
			});
    		t[i].start();
    	}
    	
    	for(int i=0; i<t.length; i++) t[i].join();
        
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

