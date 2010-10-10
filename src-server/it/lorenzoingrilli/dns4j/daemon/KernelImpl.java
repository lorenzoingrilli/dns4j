/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon;

import it.lorenzoingrilli.dns4j.daemon.util.Sperimental;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KernelImpl implements Kernel {

	private Logger logger = Logger.getLogger(KernelImpl.class.getName());
	
	public static enum SignalMode {
		BESTEFFORT,
		GUARANTEED
	};
	
	private Map<Plugin, Thread> threads = new ConcurrentHashMap<Plugin, Thread>();
	private LinkedList<Plugin> plugins= new LinkedList<Plugin>();
	private LinkedList<PluginEventReceiver> eplugins= new LinkedList<PluginEventReceiver>();
	private LinkedList<Object> components = new LinkedList<Object>();
	private ArrayBlockingQueue<Event> events = new ArrayBlockingQueue<Event>(1000, true);
	private Thread dispatchThread;
	private SignalMode signalMode = SignalMode.BESTEFFORT;
	
	public KernelImpl() {
		dispatchThread = new Thread(new Runnable() {			
			@Override
			public void run() { 
				while(!Thread.currentThread().isInterrupted()) {
					Event event = null;
					try {
						event = events.poll(200, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						return;
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
	public void init() {
		dispatchThread.start();
	}
	
	@Override
	public void destroy() {
		unloadAll();
		dispatchThread.interrupt();
	}
	
	@Override
	public void load(Object component) {
		
		if(component.getClass().getAnnotation(Deprecated.class)!=null) {
			logger.log(Level.WARNING, "Component "+component.getClass().getName()+" is DEPRECATED and will be not available in future versions. Please, consider to use a different component.");
		}
		if(component.getClass().getAnnotation(Sperimental.class)!=null) {
			logger.log(Level.WARNING, "Component "+component.getClass().getName()+" is SPERIMENTAL. Please, NOT use it in production environment");
		}
		
		components.add(component);
		
		if(component instanceof Plugin) {
			Plugin plugin = (Plugin) component;
			logger.log(Level.FINEST, "Loading plugin "+plugin);
			plugin.init(this);
			logger.log(Level.FINEST, "Loaded plugin "+plugin);
			if(plugin instanceof Runnable) {
				Thread t = new Thread((Runnable) plugin);
				t.start();
				threads.put(plugin, t);
				logger.log(Level.FINEST, "Started thread "+plugin);					 
			}

			if(plugin instanceof PluginEventReceiver)
				eplugins.add((PluginEventReceiver) plugin);
			else
				plugins.add(plugin);
		}
		else {
			logger.log(Level.FINEST, "Loading component "+component);
		}
	}

	@Override
	public void unload(Object component) {
		if(component instanceof Plugin) {
			Plugin plugin = (Plugin) component;
			Thread t = threads.get(plugin);
			if(t!=null) {
				t.interrupt();
				threads.remove(plugin);
			}
			plugin.destroy();
		}
		components.remove(component);
	}

	@Override
	public void unloadAll() {
		for(Plugin plugin: plugins) unload(plugin);
		for(PluginEventReceiver plugin: eplugins) unload(plugin);
	}

	@Override
	public void signal(Event event) {
		try {
			if(SignalMode.GUARANTEED.equals(signalMode)) {
				events.put(event);
			}
			else {
				events.offer(event);
				// add warn to stdout if queue is full! 
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Object> components() {
		List<Object> list = new LinkedList<Object>();
		list.addAll(components);
		return list;
	}

	public SignalMode getSignalMode() {
		return signalMode;
	}

	public void setSignalMode(SignalMode signalMode) {
		this.signalMode = signalMode;
	}

}
