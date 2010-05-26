package it.lorenzoingrilli.dns4j.protocol.rr;

/**
 * TXT Resource Record.
 * 
 * Defined in RFC 1035
 * 
 * @author Lorenzo Ingrilli'
 * @see <a href="ftp://ftp.rfc-editor.org/in-notes/rfc1035.txt">RFC 1035</a> 
 */
public interface Txt extends RR {
    public String getData();
    public void setData(String data);
}
