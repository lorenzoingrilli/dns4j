package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Ptr extends RR {
    public String getPtrDname();
    public void setPtrDname(String ptrDname);
}
