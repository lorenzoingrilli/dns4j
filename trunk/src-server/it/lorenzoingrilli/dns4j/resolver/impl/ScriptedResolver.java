package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.resolver.SyncResolver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class ScriptedResolver implements SyncResolver {
    
	private static Logger logger = Logger.getLogger(ScriptedResolver.class.getName());
	
    private String filename;
	private ScriptEngineManager manager;
    private ScriptEngine engine;

    public ScriptedResolver() {
    }
    
    @Override
    public Message query(Message request) {
    	File file = new File(filename);
    	
    	if(file.isDirectory() || !file.canRead()) {
    		throw new RuntimeException(file.getPath()+" must be a readable file");
    	}

        Bindings binds = engine.createBindings();
        binds.put("request", request);

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
    
    public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
        String extension = getExtension(filename);
        manager = new ScriptEngineManager();
        engine = manager.getEngineByExtension(extension);
	}
	
    private static String getExtension(String filename) {
        String fields[] = filename.split("\\.");
        return fields[fields.length-1];
    }

}
