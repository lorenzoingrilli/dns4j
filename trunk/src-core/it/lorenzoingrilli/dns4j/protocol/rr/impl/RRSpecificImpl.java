/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.rr.impl;

/**
 * @author Lorenzo Ingrilli'
 */
public class RRSpecificImpl extends RRImpl {

    public RRSpecificImpl(int clazz, int type) {
        super();
        super.setType(type);
        super.setClazz(clazz);
    }

    @Override
    public int getRdLenght() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRdata(byte[] rdata) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setType(int type) {
        throw new UnsupportedOperationException();
    }

}
