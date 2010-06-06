package it.lorenzoingrilli.dns4j.daemon;

public interface PluginManager {
	public void init();
	public void destroy();
	public void load(Plugin plugin);
	public void unload(Plugin plugin);
	public void unloadAll();
}
