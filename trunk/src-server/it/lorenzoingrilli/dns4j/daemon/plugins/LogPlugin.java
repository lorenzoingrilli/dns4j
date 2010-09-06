package it.lorenzoingrilli.dns4j.daemon.plugins;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.lorenzoingrilli.dns4j.daemon.Event;
import it.lorenzoingrilli.dns4j.daemon.EventRecv;
import it.lorenzoingrilli.dns4j.daemon.EventSent;
import it.lorenzoingrilli.dns4j.daemon.PluginEventReceiver;
import it.lorenzoingrilli.dns4j.daemon.Kernel;

public class LogPlugin implements PluginEventReceiver {

	private Logger logger = Logger.getLogger(LogPlugin.class.getName());
	
	@Override
	public void init(Kernel kernel) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void receive(Event event) {
		if(event instanceof EventRecv)
			logger.log(Level.INFO, "RECV "+((EventRecv)event).getMessage()+" ("+event.getEmitter()+")");
		else if(event instanceof EventSent)
			logger.log(Level.INFO, "SENT "+((EventSent)event).getMessage()+" ("+event.getEmitter()+")");
	}

}
