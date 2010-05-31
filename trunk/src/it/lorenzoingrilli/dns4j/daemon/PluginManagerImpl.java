package it.lorenzoingrilli.dns4j.daemon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginManagerImpl implements PluginManager, EventDispatcher{

	private Logger logger = Logger.getLogger(PluginManagerImpl.class.getName());
	
	private HashMap<Plugin, Thread> threads = new HashMap<Plugin, Thread>();
	private LinkedList<Plugin> plugins= new LinkedList<Plugin>();
	private LinkedList<PluginEventReceiver> eplugins= new LinkedList<PluginEventReceiver>();
	private ArrayBlockingQueue<Event> events = new ArrayBlockingQueue<Event>(1000, true);
	private Thread dispatchThread;
	
	public PluginManagerImpl() {
		dispatchThread = new Thread(new Runnable() {			
			@Override
			public void run() { 
				while(!Thread.currentThread().isInterrupted()) {
					Event event = null;
					try {
						event = events.poll(200, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(event!=null) {
						for(PluginEventReceiver plugin: eplugins)
							plugin.receive(event);
					}
				}				
			}
		});
		dispatchThread.setDaemon(true);
	}
	
	@Override
	public void destroy() {
		unloadAll();
		dispatchThread.interrupt();
	}

	@Override
	public void init() {
		dispatchThread.start();
	}
	
	@Override
	public void load(Plugin plugin) {
		logger.log(Level.INFO, "Loading plugin "+plugin);
		plugin.init(this);
		logger.log(Level.INFO, "Loaded plugin "+plugin);
		if(plugin instanceof Runnable) {
			Thread t = new Thread((Runnable) plugin);
			t.start();
			threads.put(plugin, t);
			logger.log(Level.INFO, "Started thread "+plugin);					 
		}
		
		if(plugin instanceof PluginEventReceiver)
			eplugins.add((PluginEventReceiver) plugin);
		else
			plugins.add(plugin);
	}

	@Override
	public void unload(Plugin plugin) {
		Thread t = threads.get(plugin);
		if(t!=null) {
			t.interrupt();
			threads.remove(plugin);
		}
		plugin.destroy();
	}

	@Override
	public void unloadAll() {
		for(Plugin plugin: plugins) unload(plugin);
		for(PluginEventReceiver plugin: eplugins) unload(plugin);
	}

	@Override
	public void dispatch(Event event) {
		try {
			events.put(event);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
