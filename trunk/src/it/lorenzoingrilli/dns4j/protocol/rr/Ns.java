package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Ns extends RR {
    public String getNsdName();
    public void setNsdName(String nsdname);
}
