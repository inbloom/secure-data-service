package org.slc.sli.modeling.xmicomp;

public class XmiCompRuntimeException extends RuntimeException {

    public XmiCompRuntimeException(Throwable cause) {
        super(cause);
    }

    public XmiCompRuntimeException(String message) {
        super(message);
    }

    public XmiCompRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
