package it.lorenzoingrilli.dns4j.daemon;

import it.lorenzoingrilli.dns4j.cli.CLI;
import it.lorenzoingrilli.dns4j.daemon.plugins.JMXPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.LogPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.TCPServerPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.UDPServerPlugin;
import it.lorenzoingrilli.dns4j.protocol.impl.SerializerImpl;
import it.lorenzoingrilli.dns4j.resolver.impl.ScriptedResolver;
import it.lorenzoingrilli.dns4j.resolver.impl.YamlResolver;

import java.io.File;
import java.io.FileReader;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * DNS Server
 *
 * @author Lorenzo Ingrilli'
 */
public class nsd extends CLI {
    
	public static final String DEFAULT_CONF = File.separator+"etc"+File.separator+"dns4j"+File.separator+"nsd.yml";
	
	public static void main(String[] args) throws Exception {    	
		nsd _nsd = new nsd(args);
		_nsd.startup();
	}

	private PluginManager pm = new PluginManagerImpl();
	
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
		 config.setClassTag("log", LogPlugin.class);		 
		 config.setClassTag("tcp", TCPServerPlugin.class);
		 config.setClassTag("udp", UDPServerPlugin.class);
		 config.setClassTag("jmx", JMXPlugin.class);
			
		 pm.init();
		 Object param = null;
		 while( (param = reader.read()) != null) {			 
			 if(param instanceof Plugin) {
				 pm.load((Plugin) param);
			 }
			 else {
				 System.out.println("Load component "+param);
			 }
	 	 }
	}
	
	@Override
	public void shutdown() {
		pm.destroy();
	}
    
}
