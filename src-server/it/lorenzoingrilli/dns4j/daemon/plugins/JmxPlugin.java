/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;

import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;

import it.lorenzoingrilli.dns4j.daemon.Plugin;
import it.lorenzoingrilli.dns4j.daemon.Kernel;

/**
 * JMX Plugin.
 * 
 * Expose dns4j component via jmx interface.
 * Warning: it does not start a jmx server connector. 
 * 
 * @author Lorenzo Ingrilli
 *
 */
public class JmxPlugin implements Plugin {
	
	private static final String MODELER_CONF = "/it/lorenzoingrilli/dns4j/daemon/util/mbeans.xml";
	
	private static Logger logger = Logger.getLogger(JmxPlugin.class.getName());
		
	private Kernel kernel;
	private long id = 0;
	
	@Override
	public void init(Kernel kernel) {
		this.kernel = kernel;
		
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		InputStream is = this.getClass().getResourceAsStream(MODELER_CONF);		
		Registry registry = Registry.getRegistry(null, null);
		
		try {
			registry.loadMetadata(is);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in jmx configuration", e);
		}
		try {
			is.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error in jmx configuration", e);
		}
		
		for(Object component: this.kernel.components()) {
			try {
				ManagedBean bean = null;
				Class<?> clazz = component.getClass();
				while(bean==null && clazz!=null) {
					bean = registry.findManagedBean(clazz.getName());
					clazz = clazz.getSuperclass();					
				}
				if(bean==null) continue;
				
				ModelMBean mbean = bean.createMBean(component);
				ObjectName name = new ObjectName(bean.getDomain()+":name="+bean.getGroup()+(++id)+",type="+bean.getGroup());
				mbs.registerMBean(mbean, name);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error in jmx setup", e);
			}
		}
	}
	
	@Override
	public void destroy() {
	}

}
