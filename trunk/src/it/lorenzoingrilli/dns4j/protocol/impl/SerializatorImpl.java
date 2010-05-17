package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Header;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Question;
import it.lorenzoingrilli.dns4j.protocol.rr.A;
import it.lorenzoingrilli.dns4j.protocol.rr.CName;
import it.lorenzoingrilli.dns4j.protocol.rr.HInfo;
import it.lorenzoingrilli.dns4j.protocol.rr.Mx;
import it.lorenzoingrilli.dns4j.protocol.rr.Ns;
import it.lorenzoingrilli.dns4j.protocol.rr.Ptr;
import it.lorenzoingrilli.dns4j.protocol.rr.RR;
import it.lorenzoingrilli.dns4j.protocol.rr.Soa;
import it.lorenzoingrilli.dns4j.protocol.rr.Txt;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class SerializatorImpl {

    private static final Charset charset = Charset.forName("ASCII");
    private static final String DOT = "\\.";

    public static int serialize(Message m, byte[] buffer) {
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

    private static void serialize(ByteBuffer bb, RR rr) {
        putDomainName(bb, rr.getName());
        putUShort(bb, rr.getType());
        putUShort(bb, rr.getClazz());
        putUInt(bb, rr.getTtl());        
        if(rr instanceof A) serialize(bb, (A) rr);
        else if(rr instanceof CName) serialize(bb, (CName) rr);
        else if(rr instanceof HInfo) serialize(bb, (HInfo) rr);
        else if(rr instanceof Mx) serialize(bb, (Mx) rr);
        else if(rr instanceof Ns) serialize(bb, (Ns) rr);
        else if(rr instanceof Ptr) serialize(bb, (Ptr) rr);
        else if(rr instanceof Soa) serialize(bb, (Soa) rr);
        else if(rr instanceof Txt) serialize(bb, (Txt) rr);
        else {
            putUShort(bb, rr.getRdLenght());
            bb.put(rr.getRdata());
        }
    }

    private static void serialize(ByteBuffer bb, A a) {
        putUShort(bb, 4);
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

    private static void serialize(ByteBuffer bb, Soa soa) {
        putUShort(bb, 
                domainNameSize(soa.getMName())+
                domainNameSize(soa.getRName())+
                20
        );
        putDomainName(bb, soa.getMName());
        putDomainName(bb, soa.getRName());
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

    private static void putDomainName(ByteBuffer bb, String name) {
        String parts[] = name.split(DOT);
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

    private static void putCharacterString(ByteBuffer bb, String name) {
        byte[] p = name.getBytes(charset);
        putUByte(bb, p.length);
        bb.put(p);
    }

   public static void putUByte(ByteBuffer bb, int value) {
      bb.put((byte)(value & 0xff));
   }

   public static void putUShort(ByteBuffer bb, int value) {
      bb.putShort((short)(value & 0xffff));
   }

   public static void putUInt(ByteBuffer bb, long value) {
      bb.putInt((int)(value & 0xffffffffL));
   }

}
