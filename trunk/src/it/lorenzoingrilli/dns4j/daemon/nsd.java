package it.lorenzoingrilli.dns4j.daemon;

import it.lorenzoingrilli.dns4j.cli.CLI;
import it.lorenzoingrilli.dns4j.daemon.plugins.LogPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.TCPServerPlugin;
import it.lorenzoingrilli.dns4j.daemon.plugins.UDPServerPlugin;
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
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class nsd extends CLI {
    
	public static final String DEFAULT_CONF = File.separator+"etc"+File.separator+"dns4j"+File.separator+"nsd.conf";
	public static final String DEFAULT_MODE = "simple";  
	
	private PluginManager pm = new PluginManagerImpl();
	
	public nsd(String args[]) {
		super();
		setArgs(args);
    	Options options = new Options();
    	options.addOption(new Option("c", "config-file", true, "configuration file"));
    	//options.addOption(new Option("m", "config-mode", true, "configuration mode (proxy/simple/advanced, default=simple)"));
    	setOptions(options);
	}
	
	@Override
	public void run() throws Exception {
		String conf = getCmdLine().getOptionValue('c', DEFAULT_CONF);
	   	 YamlReader reader = new YamlReader(new FileReader(conf));
		 YamlConfig config = reader.getConfig();
		 config.setClassTag("executor", TPExecutor.class);
		 config.setClassTag("yamlresolver", YamlResolver.class);
		 config.setClassTag("log", LogPlugin.class);
		 config.setClassTag("tcp", TCPServerPlugin.class);
		 config.setClassTag("udp", UDPServerPlugin.class);
			
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
		pm.unloadAll();
	}
    
    public static void main(String[] args) throws Exception {    	
     nsd _nsd = new nsd(args);
     _nsd.startup();
    }
    
    /*
    @SuppressWarnings({"unchecked" })
	private static void simpleNsd(String file) throws IOException {
    	
    	YamlReader reader = new YamlReader(new FileReader(file));
    	Map<String, String> conf = (Map<String, String>) reader.read();
    	
    	YamlResolver resolver = new YamlResolver(conf.get("db"));
    	resolver.setQuestionEcho(true);

    	BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(500, true);
    	ThreadFactory threadFactory = Executors.defaultThreadFactory();
    	ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 500, TimeUnit.MILLISECONDS, queue, threadFactory);

    	UDPServer udpServer = new UDPServer(5053, resolver, executor);
    	Thread tUdpServer = threadFactory.newThread(udpServer);
    	tUdpServer.start();	 

    	TCPServer tcpServer = new TCPServer(5053, resolver, executor);
    	Thread tTcpServer = threadFactory.newThread(tcpServer);
    	tTcpServer.start();
    }*/

}
