package it.lorenzoingrilli.dns4j.daemon;

public interface Plugin {
	public void init(Kernel kernel);	
	public void destroy();
}
