package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface CName extends RR {
    public String getCname();
    public void setCname(String cname);
}
