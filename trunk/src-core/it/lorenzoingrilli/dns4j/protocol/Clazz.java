/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

/**
 * RR Class code.
 * 
 * Defined in RFC 1035 (two octets).
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public class Clazz {

    public static final int IN = 1;
    public static final int CS = 2;
    public static final int CH = 3;
    public static final int HS = 4;
    
}
