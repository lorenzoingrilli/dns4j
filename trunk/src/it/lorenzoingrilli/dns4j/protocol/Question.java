package it.lorenzoingrilli.dns4j.protocol;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Question {
    public String getQname();
    public int getQtype();
    public int getQclass();

    public void setQname(String qname);
    public void setQtype(int type);
    public void setQclass(int qclass);
}
