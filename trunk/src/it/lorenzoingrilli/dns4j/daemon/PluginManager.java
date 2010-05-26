package it.lorenzoingrilli.dns4j.daemon;

public interface PluginManager {
	public void load(Plugin plugin);
	public void unload(Plugin plugin);
	public void unloadAll();
}
