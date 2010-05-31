package it.lorenzoingrilli.dns4j.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer {
    public void serialize(Message m, OutputStream os) throws IOException;
    public int serialize(Message m, byte[] buffer);
    public Message deserialize(InputStream is) throws IOException;
    public Message deserialize(byte[] buffer);
}
