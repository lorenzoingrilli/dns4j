/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon;

import java.util.List;

public interface Kernel {
	public void init();
	public void destroy();
	
	public void load(Object component);
	public void unload(Object component);	
	public void unloadAll();
	
	public void signal(Event event);
	
	public List<Object> components();
	
}
