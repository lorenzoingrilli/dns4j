package it.lorenzoingrilli.dns4j.daemon;

public interface PluginEventReceiver extends Plugin {
	public void init(EventDispatcher dispatcher);	
	public void destroy();
	public void receive(Event event);
}
