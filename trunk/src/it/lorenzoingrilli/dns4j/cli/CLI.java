package it.lorenzoingrilli.dns4j.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
		
	private Options options;
	private String args[];
	private CommandLine cmdLine;
	
	public CLI() { 
	}
	
	public void startup() throws Exception {
        try {
            CommandLineParser parser = new GnuParser();
            cmdLine = parser.parse(options, args);
        }
        catch(ParseException exp ) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("nsc", "Name Server Daemon", options, "Developed by Lorenzo Ingrilli' - http://www.lorenzoingrilli.it", true);
            System.exit(1);
        }
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		});
		run();
	}
	
	public void run() throws Exception {
		
	}
	
	public void shutdown()  {
	}
	
	public CommandLine getCmdLine() {
		return cmdLine;
	}
	
    public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}
	
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

    
}
