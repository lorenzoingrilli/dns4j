package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Header;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class HeaderImpl implements Header {

    private int id;
    private boolean query;
    private int opcode;
    private boolean authoritative;
    private boolean truncated;
    private boolean recursionDesidered;
    private boolean recursionAvailable;
    private int responseCode;
    private int z;
    private int qdcount;
    private int arcount;
    private int nscount;
    private int ancount;    

    @Override
    public String toString() {
        return 
            "Header(id="+id+", qr="+isQuery()+", opcode="+getOpcode()+
            ", aa="+isAuthoritative()+", tc="+isTruncated()+
            ", rd="+isRecursionDesidered()+", ra="+isRecursionAvailable()+
            ", z="+getZ()+", rcode="+getResponseCode()+", qd="+qdcount+
            ", an="+ancount+", ns="+nscount+", ar="+arcount+")";
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isQuery() {
        return query;
    }

    @Override
    public int getOpcode() {
        return opcode;
    }

    @Override
    public boolean isAuthoritative() {
        return authoritative;
    }

    @Override
    public boolean isTruncated() {
        return truncated;
    }

    @Override
    public boolean isRecursionDesidered() {
        return recursionDesidered;
    }

    @Override
    public boolean isRecursionAvailable() {
        return recursionAvailable;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public int getQdCount() {
        return qdcount;
    }

    @Override
    public int getAnCount() {
        return ancount;
    }

    @Override
    public int getNsCount() {
        return nscount;
    }

    @Override
    public int getArCount() {
        return arcount;
    }

    @Override
    public void setAnCount(int ancount) {
        this.ancount = ancount;
    }

    @Override
    public void setArCount(int arcount) {
        this.arcount = arcount;
    }

    @Override
    public void setAuthoritative(boolean authoritative) {
        this.authoritative = authoritative;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setNsCount(int nscount) {
        this.nscount = nscount;
    }

    @Override
    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public void setQdCount(int qdcount) {
        this.qdcount = qdcount;
    }

    @Override
    public void setQuery(boolean query) {
        this.query = query;
    }

    @Override
    public void setRecursionAvailable(boolean recursionAvailable) {
        this.recursionAvailable = recursionAvailable;
    }

    @Override
    public void setRecursionDesidered(boolean recursionDesidered) {
        this.recursionDesidered = recursionDesidered;
    }

    @Override
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    @Override
    public void setZ(int z) {
        this.z = z;
    }

}
