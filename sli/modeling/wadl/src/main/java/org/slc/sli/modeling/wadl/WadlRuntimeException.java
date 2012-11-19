package org.slc.sli.modeling.wadl;

public class WadlRuntimeException extends RuntimeException {

    public WadlRuntimeException(Throwable cause) {
        super(cause);
    }

    public WadlRuntimeException(String message) {
        super(message);
    }

    public WadlRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
