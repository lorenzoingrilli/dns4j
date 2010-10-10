/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.impl;

/**
 * @author Lorenzo Ingrilli'
 */
public class Validation {

    public static void checkUByte(long value) {
        if(value<0 || value>255)
            throw new IllegalArgumentException("UByte must be between 0 and 255");
    }

    public static void checkUShort(long value) {
        if(value<0 || value>65535)
            throw new IllegalArgumentException("UShort must be between 0 and 65535");
    }

    public static void checkUInt(long value) {
        if(value<0 || value>4294967295L)
            throw new IllegalArgumentException("UInt must be between 0 and 4294967295");
    }
    
}
