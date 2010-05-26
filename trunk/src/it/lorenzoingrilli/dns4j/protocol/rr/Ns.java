package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * NS Resource Record.
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface Ns extends RR {
    public String getNsdName();
    public void setNsdName(String nsdname);
}
