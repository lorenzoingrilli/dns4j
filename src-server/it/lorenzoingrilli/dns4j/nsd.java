package it.lorenzoingrilli.dns4j;

import it.lorenzoingrilli.dns4j.daemon.Kernel;
import it.lorenzoingrilli.dns4j.daemon.KernelImpl;
import it.lorenzoingrilli.dns4j.daemon.TPExecutor;
import it.lorenzoingrilli.dns4j.daemon.plugins.JmxPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.LogPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.TCPServerPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.UDPServerPlugin;
import it.lorenzoingrilli.dns4j.daemon.resolver.ScriptedResolver;
import it.lorenzoingrilli.dns4j.daemon.resolver.YamlResolver;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.daemon.resolver.DBAuthResolver;
import it.lorenzoingrilli.dns4j.daemon.util.CliApplication;
import it.lorenzoingrilli.dns4j.daemon.util.FileSerializer;
import it.lorenzoingrilli.dns4j.daemon.util.Inet4AddressSerializer;
import it.lorenzoingrilli.dns4j.daemon.util.Inet6AddressSerializer;
import it.lorenzoingrilli.dns4j.daemon.util.InetAddressSerializer;

import java.io.File;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.dbcp.BasicDataSource;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * DNS Server
 *
 * @author Lorenzo Ingrilli'
 */
public class nsd extends CliApplication {
    
	public static final String DEFAULT_CONF = File.separator+"etc"+File.separator+"dns4j"+File.separator+"nsd.yml";
	
	public static void main(String[] args) throws Exception {    	
		nsd _nsd = new nsd(args);
		_nsd.startup();
	}

	private Kernel kernel = new KernelImpl();	
	private Logger logger = Logger.getLogger(KernelImpl.class.getName());
	
	public nsd(String args[]) {
		super();
		setArgs(args);
    	Options options = new Options();
    	options.addOption(new Option("c", "config", true, "configuration file"));
    	setOptions(options);
	}
	
	@Override
	public void run() throws Exception {
		String conf = getCmdLine().getOptionValue('c', DEFAULT_CONF);
		YamlReader reader = new YamlReader(new FileReader(conf));
		YamlConfig config = reader.getConfig();
		config.setClassTag("executor", TPExecutor.class);
		config.setClassTag("serializer", SerializerImpl.class);
		config.setClassTag("yamlresolver", YamlResolver.class);		 
		config.setClassTag("scriptedresolver", ScriptedResolver.class);		
//		config.setClassTag("recursiveresolver", RecursiveAsyncResolver.class);
		config.setClassTag("dbresolver", DBAuthResolver.class);
		config.setClassTag("datasource", BasicDataSource.class);
		config.setClassTag("jmx", JmxPlugin.class);
		config.setClassTag("log", LogPlugin.class);		 		
		config.setClassTag("tcp", TCPServerPlugin.class);
		config.setClassTag("udp", UDPServerPlugin.class);
		config.setScalarSerializer(InetAddress.class, new InetAddressSerializer());
		
		try {
			config.setClassTag("unixsecurity", Class.forName("it.lorenzoingrilli.dns4j.daemon.plugins.UnixSecurityPlugin"));
		} catch(ClassNotFoundException e) {}
		
		config.setScalarSerializer(InetAddress.class, new InetAddressSerializer());
		config.setScalarSerializer(Inet4Address.class, new Inet4AddressSerializer());
		config.setScalarSerializer(Inet6Address.class, new Inet6AddressSerializer());
		config.setScalarSerializer(File.class, new FileSerializer());

		logger.log(Level.INFO, "DNS4J Server starting...");
		// TODO on exception during startup nsd should be auto-halted
		kernel.init();
		Object param = null;
		while( (param = reader.read()) != null) {			 
				kernel.load(param);
		}		
		logger.log(Level.INFO, "DNS4J Server started");		
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
		kernel.destroy();
		logger.log(Level.INFO, "DNS4J Server halted");
	}
    
}

