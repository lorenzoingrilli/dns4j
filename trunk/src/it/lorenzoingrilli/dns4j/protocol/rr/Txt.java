package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Txt extends RR {
    public String getData();
    public void setData(String data);
}
