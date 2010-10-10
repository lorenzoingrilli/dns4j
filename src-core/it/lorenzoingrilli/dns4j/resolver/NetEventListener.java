/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.resolver;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface NetEventListener {
    public void onSent(byte[] buffer, int offset, int len);
    public void onRecv(byte[] buffer, int offset, int len);
}
