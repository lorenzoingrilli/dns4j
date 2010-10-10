/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.resolver;

import it.lorenzoingrilli.dns4j.protocol.Message;

import java.beans.ConstructorProperties;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Lorenzo Ingrilli'
 */
public class ScriptedResolver implements ServerSyncResolver<ServerQueryContext> {
    
	private static Logger logger = Logger.getLogger(ScriptedResolver.class.getName());
	
    private File file;
	private ScriptEngineManager manager;
    private ScriptEngine engine;
    private Map<String, Object> context = new ConcurrentHashMap<String, Object>();

    @ConstructorProperties(value={"file"})
    public ScriptedResolver(File file) {
    	setFile(file);
    }
	
    @Override
    public Message query(Message request) {
    	return query(request, null);
    }
    
    @Override
    public Message query(Message request, ServerQueryContext queryContext) {   	
    	if(file.isDirectory() || !file.canRead()) {
    		throw new RuntimeException(file.getPath()+" must be a readable file");
    	}

        Bindings binds = engine.createBindings();
        binds.put("request", request);
        binds.put("context", context);
        if(queryContext!=null) {
	        binds.put("srcAddress", queryContext.getAddress());
	        binds.put("srcPort", queryContext.getPort());
        }

        FileReader reader = null;
        try {
            reader = new FileReader(file);
            Object result = engine.eval(reader, binds);
            if (result instanceof Message) {
                return (Message) result;
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if(reader!=null)
                try {
                	reader.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
        }
    }
    
    public File getFile() {
		return file;
	}

	public void setFile(File file) {		
        String extension = getExtension(file.getName());
        manager = new ScriptEngineManager();
        engine = manager.getEngineByExtension(extension);
        if(engine==null)
        	throw new RuntimeException("No script engine found for file with extension '"+extension+"'. Maybe you need additional java libraries (jar)");
        this.file = file;
	}
	
	public void setFilename(String filename) {
		setFile(new File(filename));
	}
	
	public String getFilename() {
		return file.getPath();
	}
	
    private static String getExtension(String filename) {
        String fields[] = filename.split("\\.");
        return fields[fields.length-1];
    }

}
