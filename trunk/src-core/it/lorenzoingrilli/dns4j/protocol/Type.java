/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

/**
 * constants for: Resource records type
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public class Type {

    public static final int A = 1;
    public static final int NS = 2;
    public static final int MD = 3;
    public static final int MF = 4;
    public static final int CNAME = 5;
    public static final int SOA = 6;
    public static final int MB = 7;
    public static final int MG = 8;
    public static final int MR = 9;
    public static final int NULL = 10;
    public static final int WKS = 11;
    public static final int PTR = 12;
    public static final int HINFO = 13;
    public static final int MINFO = 14;
    public static final int MX = 15;
    public static final int TXT = 16;
    public static final int AFSDB = 18;
    public static final int ISDN = 20;
    public static final int SIG = 24;
    public static final int GPOS = 27;
    public static final int LOC = 29;
    public static final int AAAA = 28;
    public static final int SRV = 33;
    public static final int NAPTR = 35;
    public static final int KX = 36;
    public static final int A6 = 38;
    public static final int DNAME = 39;
    public static final int DS = 43;
    public static final int TKEY = 249;
    public static final int TSIG = 250;
    public static final int AXFR = 252;
    public static final int MAILB = 253;
    public static final int MAILA = 254;
    public static final int ALL = 255;
    
}
