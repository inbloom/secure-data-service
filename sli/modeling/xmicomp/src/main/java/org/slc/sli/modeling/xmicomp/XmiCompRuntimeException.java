package org.slc.sli.modeling.xmicomp;

public class XmiCompRuntimeException extends RuntimeException {

    public XmiCompRuntimeException(Exception e) {
        super(e);
    }

    public XmiCompRuntimeException(String message) {
        super(message);
    }

    public XmiCompRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
