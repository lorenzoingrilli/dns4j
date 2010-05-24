package it.lorenzoingrilli.dns4j.cli;

import it.lorenzoingrilli.dns4j.net.TCPServer;
import it.lorenzoingrilli.dns4j.net.UDPServer;
import it.lorenzoingrilli.dns4j.resolver.impl.YamlResolver;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * DNS Server
 *
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class nsd {
    
	public static final String DEFAULT_CONF = File.separator+"etc"+File.separator+"dns4j"+File.separator+"nsd.yml";
	
    public static void main(String[] args) throws Exception {
         	
     YamlResolver resolver = new YamlResolver(args[0]);
	 resolver.setQuestionEcho(true);
	 
	 BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(500, true);
	 ThreadFactory threadFactory = Executors.defaultThreadFactory();
	 ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 500, TimeUnit.MILLISECONDS, queue, threadFactory);
	 /*
	 UDPServer udpServer = new UDPServer(5053, resolver, executor);
	 Thread tUdpServer = threadFactory.newThread(udpServer);
	 tUdpServer.start();	 

	 TCPServer tcpServer = new TCPServer(5053, resolver, executor);
	 Thread tTcpServer = threadFactory.newThread(tcpServer);
	 tTcpServer.start();*/	

	 YamlReader reader = new YamlReader(new FileReader(DEFAULT_CONF));
	 YamlConfig config = reader.getConfig();
	 //config.setClassTag("executor", TExecutor.class);
	 config.setClassTag("tcp", TCPServer.class);
	 config.setClassTag("udp", UDPServer.class);
		
	 LinkedList<Thread> threads = new LinkedList<Thread>(); 
	 Object param;
	 while( (param = reader.read()) != null) {
		 if(param instanceof Runnable) {
			 Thread t = new Thread((Runnable) param);
			 t.start();
			 threads.add(t);
			 System.out.println("Started "+param);
		 }
		 else 
			 System.out.println(param);
 	 }	

    }
    
    
}
