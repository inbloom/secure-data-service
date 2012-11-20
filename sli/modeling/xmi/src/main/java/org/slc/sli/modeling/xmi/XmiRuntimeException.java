package org.slc.sli.modeling.xmi;

public class XmiRuntimeException extends RuntimeException {

    public XmiRuntimeException(Throwable cause) {
        super(cause);
    }

    public XmiRuntimeException(String message) {
        super(message);
    }

    public XmiRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
