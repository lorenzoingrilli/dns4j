package it.lorenzoingrilli.dns4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * DNS4J Database install/upgrade utility
 *
 * @author Lorenzo Ingrilli'
 */
public class nsadmin {
    
	private static final String DEFAULT_JDBCURL = "jdbc:mysql://localhost:3306/dns4j";	
	private static final String DEFAULT_USERNAME = "dns4j";
	private static final String DEFAULT_PASSWORD = null;
	
	private static final String INSTALL_FILE = "/it/lorenzoingrilli/dns4j/daemon/resolver/db/mysql/install.sql";
	private static final String EXAMPLE_FILE = "/it/lorenzoingrilli/dns4j/daemon/resolver/db/mysql/example.sql";

	private static Options options = null;
	
    static {
    	options = new Options();    	
    	options.addOption(new Option("u", "username", true, "Username to connect on selected database (default: "+DEFAULT_USERNAME+")"));
    	options.addOption(new Option("p", "password", true, "Password to connect on selected database (default: none)"));
    	
    	OptionGroup g2 = new OptionGroup();
    	g2.setRequired(true);
    	g2.addOption(new Option("j", "jdbc-url", true, "JDBC URL to connect on selected database (default: "+DEFAULT_JDBCURL+")"));
    	options.addOptionGroup(g2);
    	
    	OptionGroup g1 = new OptionGroup();
    	g1.setRequired(true);
    	g1.addOption(new Option("I", "install-db", false, "Create tables on selected database"));
    	g1.addOption(new Option("U", "update-db", false, "Update tables on selected database"));
    	g1.addOption(new Option("E", "example", false, "Install example zone configuration"));    	
    	options.addOptionGroup(g1);
    }

	public static void main(String[] args) throws Exception {

    	CommandLine cmdline = parseCmdLine(options, args);
    	String jdbcUrl = cmdline.getOptionValue('j', DEFAULT_JDBCURL);
    	String username = cmdline.getOptionValue('u', DEFAULT_USERNAME);
    	String password = cmdline.getOptionValue('p', DEFAULT_PASSWORD);
    	    	
    	Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
    	
    	// INSTALL
    	if(cmdline.hasOption('I')) {
    		executeScript(connection, INSTALL_FILE);
    	}
    	// UPGRADE
    	else if(cmdline.hasOption('U')) {
    		System.err.println("Can not upgrade selected database");
    		System.exit(1);
    	}
    	// EXAMPLE
    	else if(cmdline.hasOption('E')) {
    		executeScript(connection, EXAMPLE_FILE);
    	}    	    	
    	connection.close();
    }
    
    private static CommandLine parseCmdLine(Options options, String args[])
    {   	    	 
        try {
            CommandLineParser parser = new GnuParser();
            return parser.parse( options, args );
        }
        catch(ParseException exp ) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("nsadmin", "nsadmin", options, "Developed by Lorenzo Ingrilli' - http://www.lorenzoingrilli.it", true);
            System.exit(1);
        }
        return null;
    }
    
    private static void executeScript(Connection connection, String filename) throws IOException, SQLException {
		InputStream is = nsadmin.class.getResourceAsStream(filename);
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String sql = null;
		while( (sql = r.readLine())!=null ){
			sql = sql.trim();
			if("".equals(sql)) continue;
			if(sql.endsWith(";")) sql=sql.substring(0, sql.length()-1);
			System.out.println(sql);
			Statement stmt = connection.createStatement();
			stmt.execute(sql);
			stmt.close();
		}
		is.close();
    }
    
}

