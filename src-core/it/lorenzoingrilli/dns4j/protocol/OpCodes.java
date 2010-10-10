/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

/**
 * OpCodes constants for 'opcode' field in dns header
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public class OpCodes {
	public static final int QUERY = 0;
    public static final int IQUERY = 1;
    public static final int STATUS = 2;
    public static final int NOTIFY = 4;
    public static final int UPDATE = 5;
}
