package it.lorenzoingrilli.dns4j.resolver.impl;

import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.protocol.impl.QuestionImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AAAAImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.CNameImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.HInfoImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.MxImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.NsImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.PtrImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SoaImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.TxtImpl;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class YamlResolver extends AuthoritativeResolver {
		
	public static void main(String args[]) throws Exception {
		YamlResolver r = new YamlResolver("...");
		r.setQuestionEcho(true);
		
    	Message req = 
    		new MessageBuilder()
    		.setRecursionDesidered(true)
    		.addQuestion(new QuestionImpl("www.example.net", Type.TXT, Clazz.IN))
    		.message();
    	
		Message resp = r.query(req);
		
		System.out.println("REQUEST  = "+req);
		System.out.println("RESPONSE = "+resp);
	}
	
	private String file;
	private HashMap<ZoneEntryKey, List<RR>> map = new HashMap<ZoneEntryKey, List<RR>>();

	public YamlResolver(String file) throws IOException {
		this.file = file;
		parse();
	}
	
	public void parse() throws IOException {		
		YamlReader reader = new YamlReader(new FileReader(file));
		YamlConfig config = reader.getConfig();
		config.setClassTag("zone", Zone.class);
		config.setClassTag("soa", SoaImpl.class);
		config.setClassTag("a", AImpl.class);
		config.setClassTag("aaaa", AAAAImpl.class);
		config.setClassTag("cname", CNameImpl.class);
		config.setClassTag("hinfo", HInfoImpl.class);
		config.setClassTag("ns", NsImpl.class);
		config.setClassTag("mx", MxImpl.class);
		config.setClassTag("ptr", PtrImpl.class);
		config.setClassTag("txt", TxtImpl.class);
		Zone zone = null;
		while( (zone = (Zone) reader.read()) != null) {
				Set<Entry<ZoneEntryKey, List<RR>>> es = zone.getMap().entrySet();				
				for(Entry<ZoneEntryKey, List<RR>> e: es) {
					map.put(e.getKey(), e.getValue());
				}
		}	
	}
	
	@Override
	public QuestionResponse query(Question q) {
		QuestionResponse qr = null;
		String name = q.getQname();
		//int n = name.indexOf('.');
		//String hostname = name.substring(0, n);
		//String zonename = name.substring(n+1);
		ZoneEntryKey k = new ZoneEntryKey(name, q.getQclass(), q.getQtype());
		List<RR> r = map.get(k);
		if(r!=null)
			for(RR rr: r) {
				if(qr==null)
					qr = new QuestionResponse();
				qr.getAnswer().add(rr);
			}
		return qr;
	}

}

