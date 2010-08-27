package it.lorenzoingrilli.dns4j;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.resolver.impl.DNSClient;

import java.net.InetAddress;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

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
    	options.addOption(new Option("T", "timeout", true, "timeout (in milliseconds) for a query (default 5 second)"));
    	options.addOption(new Option("n", "name", true, "Name to query (mandatory)"));
    	options.addOption(new Option("t", "type", true, "query type (default A)"));
    	options.addOption(new Option("c", "class", true, "query class (default INET)"));    	
    	options.addOption(new Option("N", "threads", true, "thread number"));
    }

	public static void main(String[] args) throws Exception {

    	CommandLine cmdline = parseCmdLine(options, args);
    	final int port = Integer.parseInt(cmdline.getOptionValue('p', DEFAULT_PORT));
    	final InetAddress host = InetAddress.getByName(cmdline.getOptionValue('s', DEFAULT_SERVER).toLowerCase());
    	String name = cmdline.getOptionValue('n');
    	int type = Integer.parseInt(cmdline.getOptionValue('t', Type.A+""));
    	int clazz = Integer.parseInt(cmdline.getOptionValue('c', Clazz.IN+""));
    	int threads = Integer.parseInt(cmdline.getOptionValue('N', "1"));  
    	final int timeout = Integer.parseInt(cmdline.getOptionValue('T', "5000"));

    	final Vector<Long> times = new Vector<Long>();
    	final AtomicLong timeouts = new AtomicLong();
    	
    	Thread t[] = new Thread[threads];
    	for(int j=0; j<threads; j++) {        	
       		final Message req =
       			new MessageBuilder()   		
       			.question()
       			.addQuestion(name, type, clazz)
       			.recursionDesidered()
       			.message();
       		t[j] = new Thread(new Runnable() {				
        			@Override
        			public void run() {
    	    			DNSClient client = new DNSClient();
    	    			client.addServer(host, port);    	
    	    			client.setTimeout(timeout);
    	    			client.setNumAttempts(1);
    	    			long ts1 = System.currentTimeMillis();   	    			
    	    			client.query(req);
    	    			long ts2 = System.currentTimeMillis();
    	    			long dt = ts2-ts1;
    	    			if(dt<timeout) {
    	    				times.add(dt);
    	    			}
    	    			else {
    	    				timeouts.incrementAndGet();
    	    			}
        			}
        		});
        	}
    	long ts1 = System.currentTimeMillis();    	
    	for(int i=0; i<t.length; i++) t[i].start();    	
    	for(int i=0; i<t.length; i++) t[i].join();    	
        long ts2 = System.currentTimeMillis(); 
        long dt = ts2-ts1;
        
    	long maxTime = Long.MIN_VALUE;
    	long minTime = Long.MAX_VALUE;
    	double avgTime = 0;    	
    	for(long v: times) {
    		if(v>maxTime) maxTime=v;
    		if(v<minTime) minTime=v;
    		avgTime += v;
    	} 
    	avgTime /= times.size();
    	
    	double varTime = 0;
    	double stdTime = 0;
    	for(long v: times) {
    		varTime += Math.pow(v-avgTime, 2);
    	} 
    	varTime = varTime/times.size();
    	stdTime = Math.sqrt(varTime);
    	
    	double avgTime2 = 0; 
    	int avgTime2Count = 0;
    	for(long v: times) {
    		if(v-avgTime<=stdTime) {
    			avgTime2 += v;
    			avgTime2Count++;
    		}
    	} 
    	avgTime2 /= avgTime2Count;
    	
    	System.out.println();
    	System.out.println(times);
    	System.out.println("total time : "+dt);
    	System.out.println("min        : "+minTime);
    	System.out.println("max        : "+maxTime);
    	System.out.println("avg        : "+avgTime);
    	System.out.println("var        : "+varTime);
    	System.out.println("std        : "+stdTime);    	
    	System.out.println("avg2       : "+avgTime2);
    	System.out.println("timeouts   : "+timeouts.get());
        
    	/*
    	long times[] = new long[count];
    	for(int j=0; j<count; j++) {
        	Thread t[] = new Thread[threads];
        	for(int i=0; i<t.length; i++) {
        		final Message req =
        			new MessageBuilder()   		
        			.question()
        			.addQuestion(name, type, clazz)
        			.recursionDesidered()
        			.message();
        		t[i] = new Thread(new Runnable() {				
        			@Override
        			public void run() {
    	    			DNSClient client = new DNSClient();
    	    			client.addServer(host, port);    	
    	    			client.setTimeout(timeout);
    	    			client.setNumAttempts(1);
    	    			long ts1 = System.currentTimeMillis();   	    			
    	    			client.query(req);
    	    			long ts2 = System.currentTimeMillis();
    	    			long t = ts2-ts1;
        			}
        		});
        	}
        	
        	long ts1 = System.currentTimeMillis();    	
        	for(int i=0; i<t.length; i++) t[i].start();    	
        	for(int i=0; i<t.length; i++) t[i].join();    	
            long ts2 = System.currentTimeMillis();        	
            times[j] = ts2-ts1;
    	}    	
    	long maxTime = Long.MIN_VALUE;
    	long minTime = Long.MAX_VALUE;
    	double avgTime = 0;
    	int avgCount = 0;
    	int timeouts = 0;
    	for(int j=0; j<times.length; j++) {
    		if(times[j]>maxTime) maxTime=times[j];
    		if(times[j]<minTime) minTime=times[j];
    		if(times[j]<=timeout) {
	    		avgTime += times[j];
	    		avgCount++;
	    		System.out.print(times[j]+" ");
    		}
    		else {
    			timeouts++;
    			System.out.print("- ");    			
    		}
    	}    	
    	avgTime = avgTime / avgCount;
    	
    	System.out.println();
    	System.out.println("min      : "+minTime);
    	System.out.println("max      : "+maxTime);
    	System.out.println("avg      : "+avgTime);
    	System.out.println("timeouts : "+timeouts);*/
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

