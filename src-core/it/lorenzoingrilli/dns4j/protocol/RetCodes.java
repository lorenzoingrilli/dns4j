/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

/**
 * Return codes constants for 'opcode' field in dns header
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public class RetCodes {
    public static final int OK = 0;
    public static final int FORMAT_ERROR = 1;
    public static final int FAILURE = 2;
    public static final int NOTFOUND = 3;
    public static final int UNIMPLEMENTED = 4;
    public static final int REFUSED = 5;
    
    // DNS Update related
    public static final int YXDOMAIN = 6;
    public static final int YXRRSET=7;
    public static final int NXRRSET=8;
    public static final int NOTAUTH=9;
    public static final int NOTZONE=10;

    // TSIG related
    public static final int BADSIG = 16;
    public static final int BADKEY = 17;
    public static final int BADTIME = 18;

}
