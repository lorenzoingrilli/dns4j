package it.lorenzoingrilli.dns4j.protocol;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public interface Header {
    public static final int RET_OK = 0;
    public static final int RET_ERR_FORMAT = 1;
    public static final int RET_ERR_FAILURE = 2;
    public static final int RET_ERR_NAME = 3;
    public static final int RET_ERR_NOTIMPL = 4;
    public static final int RET_ERR_REFUSED = 5;

    public static final boolean QUESTION = false;
    public static final boolean ANSWER = true;

    public int getId();
    public boolean isQuery();
    public int getOpcode();    
    public boolean isAuthoritative();
    public boolean isTruncated();
    public boolean isRecursionDesidered();
    public boolean isRecursionAvailable();
    public int getZ();
    public int getResponseCode();
    public int getQdCount();
    public int getAnCount();
    public int getNsCount();
    public int getArCount();

    public void setId(int id);
    public void setQuery(boolean flag);
    public void setOpcode(int opCode);
    public void setAuthoritative(boolean flag);
    public void setTruncated(boolean flag);
    public void setRecursionDesidered(boolean flag);
    public void setRecursionAvailable(boolean flag);
    public void setZ(int z);
    public void setResponseCode(int responseCode);
    public void setQdCount(int qdcount);
    public void setAnCount(int ancount);
    public void setNsCount(int nscount);
    public void setArCount(int arcount);
}
