package it.lorenzoingrilli.dns4j.protocol.rr.impl;

/**
 * @author Lorenzo Ingrilli' <info@lorenzoingrilli.it>
 */
public class RRSpecificImpl extends RRImpl {

    public RRSpecificImpl(int type) {
        super();
        super.setType(type);
    }

    @Override
    public int getRdLenght() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRdata(byte[] rdata) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setType(int type) {
        throw new UnsupportedOperationException();
    }

}
