/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.Serializer;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.rr.A;
import it.lorenzoingrilli.dns4j.protocol.rr.AAAA;
import it.lorenzoingrilli.dns4j.protocol.rr.CName;
import it.lorenzoingrilli.dns4j.protocol.rr.HInfo;
import it.lorenzoingrilli.dns4j.protocol.rr.Mx;
import it.lorenzoingrilli.dns4j.protocol.rr.Ns;
import it.lorenzoingrilli.dns4j.protocol.rr.Ptr;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;
import it.lorenzoingrilli.dns4j.protocol.rr.Soa;
import it.lorenzoingrilli.dns4j.protocol.rr.Srv;
import it.lorenzoingrilli.dns4j.protocol.rr.Txt;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AAAAImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.AImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.CNameImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.HInfoImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.MxImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.NsImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.PtrImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.RRImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SoaImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.SrvImpl;
import it.lorenzoingrilli.dns4j.protocol.rr.impl.TxtImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Lorenzo Ingrilli'
 */
public class SerializerImpl implements Serializer {

    private static final Charset charset = Charset.forName("ASCII");
    private static final String DOT_SPLIT = "\\.";
    private static final String DOT = ".";
    private static final int TCP_BUFFER_SIZE = 65535;

    @Override
    public void serialize(Message m, OutputStream os) throws IOException {
    	byte[] msgbuff = new byte[TCP_BUFFER_SIZE];
    	int len = serialize(m, msgbuff);
    	byte[] array = new byte[2];
    	ByteBuffer bb = ByteBuffer.wrap(array);
    	putUShort(bb, len);
		os.write(array);
		os.write(msgbuff);
    }
    
    @Override
    public int serialize(Message m, byte[] buffer) {
    	ByteBuffer bb = ByteBuffer.wrap(buffer);
    	Header h = m.getHeader();
        
    	int f1 = 0;
        if(h.isQuery()) f1 |= 0x80;
        f1 |= (h.getOpcode() & 0x0F) << 6;
        if(h.isAuthoritative()) f1 |= 0x04;
        if(h.isTruncated()) f1 |= 0x02;
        if(h.isRecursionDesidered()) f1 |= 0x01;                
        int f2 = 0;
        if(h.isRecursionAvailable()) f2 |= 0x80;
        f2 |= (h.getOpcode() & 0x0F);
        
    	try {        
        putUShort(bb, h.getId());
        putUByte(bb, f1);
        putUByte(bb, f2);
        putUShort(bb, h.getQdCount());
        putUShort(bb, h.getAnCount());
        putUShort(bb, h.getNsCount());
        putUShort(bb, h.getArCount());

        for(Question question: m.getQuestions()) {
            putDomainName(bb, question.getQname());
            putUShort(bb, question.getQtype());
            putUShort(bb, question.getQclass());
        }

        for(RR rr: m.getAnswer()) {
            serialize(bb, rr);
        }

        for(RR rr: m.getAuthority()) {
            serialize(bb, rr);
        }

        for(RR rr: m.getAdditional()) {
            serialize(bb, rr);
        }

        return bb.position();
    	}
    	catch(BufferOverflowException e) {
    		// Set truncation flag
    		int len = bb.position();
    		bb.position(2);
            f1 |= 0x02;
            putUByte(bb, f1);
    		return len;
    	}
    }
    
    @Override
    public Message deserialize(InputStream is) throws IOException {
    	byte[] array = new byte[2];
    	is.read(array);
    	ByteBuffer bb = ByteBuffer.wrap(array);
    	int len = getUShort(bb);
    	byte buffer[] = new byte[len];
    	int letti = 0;
    	int r = 0;
    	while(r>=0 && letti<len) {
    		r = is.read(buffer, letti, buffer.length-letti);
    		letti += r;
    	}
    	return deserialize(buffer, 0, len);
    }
    
    @Override
    public Message deserialize(byte[] buffer, int offset, int lenght) {
        MessageImpl m = new MessageImpl();
        ByteBuffer bb = ByteBuffer.wrap(buffer, offset, lenght);
        
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

    private static void serialize(ByteBuffer bb, RR rr) {
        putDomainName(bb, rr.getName());
        putUShort(bb, rr.getType());
        putUShort(bb, rr.getClazz());
        putUInt(bb, rr.getTtl());        
        if(rr instanceof A) serialize(bb, (A) rr);
        else if(rr instanceof AAAA) serialize(bb, (AAAA) rr);
        else if(rr instanceof CName) serialize(bb, (CName) rr);
        else if(rr instanceof HInfo) serialize(bb, (HInfo) rr);
        else if(rr instanceof Mx) serialize(bb, (Mx) rr);
        else if(rr instanceof Ns) serialize(bb, (Ns) rr);
        else if(rr instanceof Ptr) serialize(bb, (Ptr) rr);
        else if(rr instanceof Soa) serialize(bb, (Soa) rr);
        else if(rr instanceof Txt) serialize(bb, (Txt) rr);
        else if(rr instanceof Srv) serialize(bb, (Srv) rr);
        else {
            putUShort(bb, rr.getRdLenght());
            bb.put(rr.getRdata());
        }
    }

    private static void serialize(ByteBuffer bb, A a) {
        putUShort(bb, 4);
        bb.put(a.getAddress().getAddress());
    }
    
    private static void serialize(ByteBuffer bb, AAAA a) {
        putUShort(bb, 16);
        bb.put(a.getAddress().getAddress());
    }

    private static void serialize(ByteBuffer bb, CName cname) {
        putUShort(bb, domainNameSize(cname.getCname()));
        putDomainName(bb, cname.getCname());
    }
    
    private static void serialize(ByteBuffer bb, HInfo hinfo) {
        putUShort(bb,
                domainNameSize(hinfo.getCpu())+
                domainNameSize(hinfo.getHost())
        );
        putDomainName(bb, hinfo.getCpu());
        putDomainName(bb, hinfo.getHost());
    }

    private static void serialize(ByteBuffer bb, Mx mx) {
        putUShort(bb, domainNameSize(mx.getExchange())+2);
        putUShort(bb, mx.getPreference());
        putDomainName(bb, mx.getExchange());
    }

    private static void serialize(ByteBuffer bb, Ns ns) {
        putUShort(bb, domainNameSize(ns.getNsdName()));
        putDomainName(bb, ns.getNsdName());
    }

    private static void serialize(ByteBuffer bb, Ptr ptr) {
        putUShort(bb, domainNameSize(ptr.getPtrDname()));
        putDomainName(bb, ptr.getPtrDname());
    }
    
    private static void serialize(ByteBuffer bb, Srv srv) {
        putUShort(bb, domainNameSize(srv.getTarget())+6);
        putUShort(bb, srv.getPriority());
        putUShort(bb, srv.getWeight());
        putUShort(bb, srv.getPort());
        putDomainName(bb, srv.getTarget());
    }

    private static RR deserializeRR(ByteBuffer bb){
        String name = getDomainName(bb);
        int type = getUShort(bb);
        int clazz = getUShort(bb);
        long ttl = getUInt(bb);
        int len = getUShort(bb);
        RR rr = null;
        switch(type) {
            case Type.A:	rr = deserializeA(bb); break;
            case Type.AAAA: rr = deserializeAAAA(bb); break;
            case Type.CNAME:rr = deserializeCName(bb); break;
            case Type.MX:	rr = deserializeMx(bb); break;
            case Type.NS:	rr = deserializeNs(bb); break;
            case Type.SOA:	rr = deserializeSoa(bb); break;
            case Type.PTR:	rr = deserializePtr(bb); break;
            case Type.HINFO:rr = deserializeHInfo(bb); break;
            case Type.TXT:	rr = deserializeTxt(bb); break;
            case Type.SRV:	rr = deserializeSrv(bb); break;
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
    
    private static RR deserializeAAAA(ByteBuffer bb) {
    	try {
	        byte addr[] = new byte[16];
	        bb.get(addr);
	        AAAA a = new AAAAImpl();
	        a.setAddress((Inet6Address) Inet6Address.getByAddress(addr));        
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

    private static RR deserializeSrv(ByteBuffer bb){
    	Srv srv = new SrvImpl();
        srv.setPriority(getUShort(bb));
        srv.setWeight(getUShort(bb));
        srv.setPort(getUShort(bb));
        return srv;
    }
    
    private static void serialize(ByteBuffer bb, Soa soa) {
        putUShort(bb, 
                domainNameSize(soa.getMname())+
                domainNameSize(soa.getRname())+
                20
        );
        putDomainName(bb, soa.getMname());
        putDomainName(bb, soa.getRname());
        putUInt(bb, soa.getSerial());
        putUInt(bb, soa.getRefresh());
        putUInt(bb, soa.getRetry());
        putUInt(bb, soa.getExpire());
        putUInt(bb, soa.getMinimum());
    }

    private static void serialize(ByteBuffer bb, Txt txt) {
        putUShort(bb, 
                characterStringSize(txt.getData())
        );
        putCharacterString(bb, txt.getData());
    }
    
    private static int domainNameSize(String s) {
    	return s.length()+2;
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

    private static void putDomainName(ByteBuffer bb, String name) {
    	String parts[] = name.split(DOT_SPLIT);
    	for(String part: parts) {
    		byte[] p = part.getBytes(charset);
    		putUByte(bb, p.length);
    		bb.put(p);
    	}
    	bb.put((byte) 0);
    }

    private static int characterStringSize(String s) {
    	return s.length()+1;
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

    private static void putCharacterString(ByteBuffer bb, String name) {
    	byte[] p = name.getBytes(charset);
    	putUByte(bb, p.length);
    	bb.put(p);
    }

    private static void putUByte(ByteBuffer bb, int value) {
    	bb.put((byte)(value & 0xff));
    }

    private static short getUByte(ByteBuffer bb) {
    	return((short)(bb.get() & 0xff));
    }

    private static void putUShort(ByteBuffer bb, int value) {
    	bb.putShort((short)(value & 0xffff));
    }

    private static int getUShort(ByteBuffer bb) {
    	return(bb.getShort() & 0xffff);
    }

    private static void putUInt(ByteBuffer bb, long value) {
    	bb.putInt((int)(value & 0xffffffffL));
    }

    private static long getUInt(ByteBuffer bb) {
    	return((long)bb.getInt() & 0xffffffffL);
    }

}
