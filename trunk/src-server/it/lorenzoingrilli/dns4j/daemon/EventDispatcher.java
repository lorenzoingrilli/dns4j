package it.lorenzoingrilli.dns4j.daemon;

public interface EventDispatcher {
	public void dispatch(Event event);
}
