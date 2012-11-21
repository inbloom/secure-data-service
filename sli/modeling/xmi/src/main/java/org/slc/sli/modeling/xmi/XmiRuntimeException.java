package org.slc.sli.modeling.xmi;

/**
 * XMI exception.
 */
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
