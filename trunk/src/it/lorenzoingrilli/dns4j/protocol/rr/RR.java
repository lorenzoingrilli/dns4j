package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface RR {

    public String getName();
    public int getType();
    public int getClazz();
    public long getTtl();
    public int getRdLenght();
    public byte[] getRdata();

    public void setName(String name);
    public void setType(int type);
    public void setClazz(int clazz);
    public void setTtl(long ttl);
    public void setRdata(byte[] rdata);
}
