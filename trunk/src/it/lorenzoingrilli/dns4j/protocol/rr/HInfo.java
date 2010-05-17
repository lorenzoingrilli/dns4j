package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface HInfo extends RR {
    public String getCpu();
    public void setCpu(String cpu);
    public String getHost();
    public void setHost(String host);
}
