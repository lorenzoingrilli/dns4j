package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class Validation {

    public static void checkUByte(long value) {
        if(value<0 || value>255)
            throw new IllegalArgumentException("UByte must be between 0 and 255");
    }

    public static void checkUShort(long value) {
        if(value<0 || value>65535)
            throw new IllegalArgumentException("UByte must be between 0 and 65535");
    }

    public static void checkUInt(long value) {
        if(value<0 || value>4294967295L)
            throw new IllegalArgumentException("UByte must be between 0 and 4294967295");
    }

    public static boolean match(Message request, Message response) {
        return request.getHeader().getId()==response.getHeader().getId();
    }
    
}
