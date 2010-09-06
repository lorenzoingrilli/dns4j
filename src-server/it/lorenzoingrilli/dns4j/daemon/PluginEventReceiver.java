package it.lorenzoingrilli.dns4j.daemon;

public interface PluginEventReceiver extends Plugin {	
	public void receive(Event event);
}
