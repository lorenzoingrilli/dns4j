package it.lorenzoingrilli.dns4j.daemon;

public interface Plugin {
	public void init(EventDispatcher dispatcher);	
	public void destroy();
}
