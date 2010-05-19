package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.A;
import it.lorenzoingrilli.dns4j.protocol.rr.CName;
import it.lorenzoingrilli.dns4j.protocol.rr.HInfo;
import it.lorenzoingrilli.dns4j.protocol.rr.Mx;
import it.lorenzoingrilli.dns4j.protocol.rr.Ns;
import it.lorenzoingrilli.dns4j.protocol.rr.Ptr;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;
import it.lorenzoingrilli.dns4j.protocol.rr.Soa;
import it.lorenzoingrilli.dns4j.protocol.rr.Txt;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.CNameImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.HInfoImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.MxImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.NsImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.PtrImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.RRImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SoaImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.TxtImpl;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class DeserializatorImpl {

    private static final Charset charset = Charset.forName("ASCII");
    private static final char DOT = '.';
    
    public static Message deserialize(byte[] buffer) {
        MessageImpl m = new MessageImpl();
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        
        // Header
        Header h = m.getHeader();
        h.setId(getUShort(bb));
        short f1 = getUByte(bb);
        short f2 = getUByte(bb);
        h.setQuery((f1 & 0x80)==0x80);
        h.setOpcode(f1 & 0x78);
        h.setAuthoritative((f1 & 0x04)==0x04);
        h.setTruncated((f1 & 0x02)==0x02);
        h.setRecursionDesidered((f1 & 0x01)==0x01);
        h.setRecursionAvailable((f2 & 0x80)==0x80);
        h.setZ(f2 & 0x70);
        h.setResponseCode(f2 & 0x0f);
        h.setQdCount(getUShort(bb));
        h.setAnCount(getUShort(bb));
        h.setNsCount(getUShort(bb));
        h.setArCount(getUShort(bb));

        // Question
        for(int i=0; i<h.getQdCount(); i++) {
            Question question = new QuestionImpl();
            question.setQname(getDomainName(bb));
            question.setQtype(getUShort(bb));
            question.setQclass(getUShort(bb));            
            m.getQuestions().add(question);
        }

        // Answer
        for(int i=0; i<h.getAnCount(); i++) {
            RR rr = deserializeRR(bb);
            m.getAnswer().add(rr);
        }

        // Authority
        for(int i=0; i<h.getNsCount(); i++) {
            RR rr = deserializeRR(bb);
            m.getAnswer().add(rr);
        }

        // Additional
        for(int i=0; i<h.getArCount(); i++) {
            RR rr = deserializeRR(bb);
            m.getAnswer().add(rr);
        }

        return m;
    }

    private static RR deserializeRR(ByteBuffer bb){
        String name = getDomainName(bb);
        int type = getUShort(bb);
        int clazz = getUShort(bb);
        long ttl = getUInt(bb);
        int len = getUShort(bb);
        RR rr = null;
        switch(type) {
            case Type.A: rr = deserializeA(bb); break;
            case Type.CNAME: rr = deserializeCName(bb); break;
            case Type.MX: rr = deserializeMx(bb); break;
            case Type.NS: rr = deserializeNs(bb); break;
            case Type.SOA: rr = deserializeSoa(bb); break;
            case Type.PTR: rr = deserializePtr(bb); break;
            case Type.HINFO: rr = deserializeHInfo(bb); break;
            case Type.TXT: rr = deserializeTxt(bb); break;
            default:
                byte[] rdata = new byte[len];
                bb.get(rdata);
                rr = new RRImpl();
                rr.setRdata(rdata);
                break;
        }
        rr.setName(name);        
        rr.setClazz(clazz);
        rr.setTtl(ttl);

        try {
          rr.setType(type);
        }
        catch(UnsupportedOperationException e) {
        }

        return rr;
    }

    private static RR deserializeA(ByteBuffer bb) {
    	try {
	        byte addr[] = new byte[4];
	        bb.get(addr);
	        A a = new AImpl();
	        a.setAddress((Inet4Address) Inet4Address.getByAddress(addr));        
	        return a;
    	}
    	catch(UnknownHostException e) {
    		throw new RuntimeException(e);
    	}    	
    }

    private static RR deserializeCName(ByteBuffer bb){
        String name = getDomainName(bb);
        CName cname = new CNameImpl();
        cname.setCname(name);
        return cname;
    }
    
    private static RR deserializeMx(ByteBuffer bb){
    	int preference = getUShort(bb);
        String exchange = getDomainName(bb);
        Mx mx = new MxImpl();        
        mx.setPreference(preference);
        mx.setExchange(exchange);
        return mx;
    }
    
    private static RR deserializeNs(ByteBuffer bb){
        String name = getDomainName(bb);
        Ns ns = new NsImpl();
        ns.setNsdName(name);
        return ns;
    }
    
    private static RR deserializePtr(ByteBuffer bb){
        String name = getDomainName(bb);
        Ptr ptr = new PtrImpl();
        ptr.setPtrDname(name);
        return ptr;
    }

    private static RR deserializeSoa(ByteBuffer bb){
        Soa soa = new SoaImpl();
        soa.setMname(getDomainName(bb));
        soa.setRname(getDomainName(bb));
        soa.setSerial(getUInt(bb));
        soa.setRefresh(getUInt(bb));
        soa.setRetry(getUInt(bb));
        soa.setExpire(getUInt(bb));
        soa.setMinimum(getUInt(bb));
        return soa;
    }
    
    private static RR deserializeHInfo(ByteBuffer bb){    	
        HInfo hinfo = new HInfoImpl();
        hinfo.setCpu(getCharacterString(bb));
        hinfo.setHost(getCharacterString(bb));
        return hinfo;
    }
    
    private static RR deserializeTxt(ByteBuffer bb){    	
        Txt txt = new TxtImpl();
        txt.setData(getCharacterString(bb));
        return txt;
    }

    private static String getCharacterString(ByteBuffer bb) {
        StringBuilder qname = new StringBuilder();
        for(short len=getUByte(bb); len>0; len=getUByte(bb)) {
                byte[] word = new byte[len];
                bb.get(word);
                qname.append(new String(word, charset));
        }
        return qname.toString();
    }

    private static String getDomainName(ByteBuffer bb) {
        StringBuilder qname = new StringBuilder();
        for(short len=getUByte(bb); len>0; len=getUByte(bb)) {
            if((len & 0xC0) == 0xC0) { // pointer
                short l1 = (short) (len & 0x3f);
                short l2 = getUByte(bb);
                short p = (short) (l1 << 6 | l2);
                int old_pos = bb.position();
                bb.position(p);
                qname.append(getDomainName(bb));
                bb.position(old_pos);
                qname.append(DOT);
                break;
            }
            else {
                byte[] word = new byte[len];
                bb.get(word);
                qname.append(new String(word, charset));
                qname.append(DOT);
            }
        }
        return qname.substring(0, Math.max(qname.length()-1, 0));
    }

   public static short getUByte(ByteBuffer bb) {
      return((short)(bb.get() & 0xff));
   }

   public static int getUShort(ByteBuffer bb) {
      return(bb.getShort() & 0xffff);
   }

   public static long getUInt(ByteBuffer bb) {
      return((long)bb.getInt() & 0xffffffffL);
   }

}
