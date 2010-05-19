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
	
    private File file;
    private ScriptEngineManager manager;
    private ScriptEngine engine;

    public ScriptedResolver(File file) {
        this.file = file;
        String extension = getExtension(file);
        manager = new ScriptEngineManager();
        engine = manager.getEngineByExtension(extension);
    }
    
    @Override
    public Message query(Message request) {
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
    
    private static String getExtension(File file) {
        String fields[] = file.getName().split("\\.");
        return fields[fields.length-1];
    }

}
