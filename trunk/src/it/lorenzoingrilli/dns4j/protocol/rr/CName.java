package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * CNAME Resource Record.
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface CName extends RR {
    public String getCname();
    public void setCname(String cname);
}
