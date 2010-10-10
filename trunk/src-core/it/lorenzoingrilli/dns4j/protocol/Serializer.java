/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializator/Deserializator Interface.
 * 
 * Convert a dns packet in a Message instance and viceversa, so you can avoid to manipulate byte/bits.
 * 
 * @author Lorenzo Ingrilli'
 *
 */
public interface Serializer {
    public void serialize(Message m, OutputStream os) throws IOException;
    public int serialize(Message m, byte[] buffer);
    public Message deserialize(InputStream is) throws IOException;
    public Message deserialize(byte[] buffer, int offset, int lenght);
}
