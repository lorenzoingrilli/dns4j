package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Soa extends RR {
    public String getMname();
    public void setMname(String mname);
    public String getRname();
    public void setRname(String rname);
    public long getSerial();
    public void setSerial(long serial);
    public long getRefresh();
    public void setRefresh(long refresh);
    public long getRetry();
    public void setRetry(long retry);
    public long getExpire();
    public void setExpire(long expire);
    public long getMinimum();
    public void setMinimum(long minimum);
}
