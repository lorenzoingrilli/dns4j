package it.lorenzoingrilli.dns4j.daemon;

import java.util.List;

public interface Kernel {
	public void init();
	public void destroy();
	
	public void load(Object component);
	public void unload(Object component);	
	public void unloadAll();
	
	public void dispatch(Event event);
	
	public List<Object> components();
	
}
