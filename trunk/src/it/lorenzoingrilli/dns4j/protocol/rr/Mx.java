package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Mx extends RR {
    public int getPreference();
    public void setPreference(int preference);
    public String getExchange();
    public void setExchange(String exchange);
}
