package org.slc.sli.modeling.xmi;

public class XmiRuntimeException extends RuntimeException {

    public XmiRuntimeException(Exception e) {
        super(e);
    }

    public XmiRuntimeException(String message) {
        super(message);
    }

    public XmiRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
