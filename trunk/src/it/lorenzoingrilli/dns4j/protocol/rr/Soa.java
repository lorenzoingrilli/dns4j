package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Soa extends RR {
    public String getMName();
    public String getRName();
    public int getSerial();
    public int getRefresh();
    public int getRetry();
    public int getExpire();
    public int getMinimum();
}
