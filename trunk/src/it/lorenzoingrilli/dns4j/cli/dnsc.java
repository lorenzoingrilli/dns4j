package it.lorenzoingrilli.dns4j.cli;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.protocol.impl.QuestionImpl;
import it.lorenzoingrilli.dns4j.resolver.impl.UDPSyncClient;

import java.net.InetAddress;
import java.util.Random;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class dnsc {

    public static void main(String[] args) throws Exception {
    	
    	UDPSyncClient client = new UDPSyncClient(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
    	client.open();

    	Message req = 
    		new MessageBuilder()
    		.setId(new Random().nextInt())
    		.setRecursionDesidered(true)
    		.addQuestion(new QuestionImpl(args[2], Type.A, Clazz.IN))
    		.message();
    	
        Message resp = client.query(req);
        
    	client.close();
    	
    	System.out.println("REQUEST "+req);
    	System.out.println("RESPONSE "+resp);
    }

}
