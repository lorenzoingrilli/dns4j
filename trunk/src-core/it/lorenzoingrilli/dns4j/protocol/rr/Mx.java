package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * MX Resource Record.
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface Mx extends RR {
    public int getPreference();
    public void setPreference(int preference);
    public String getExchange();
    public void setExchange(String exchange);
}
