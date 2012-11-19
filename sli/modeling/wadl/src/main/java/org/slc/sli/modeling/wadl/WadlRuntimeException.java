package org.slc.sli.modeling.wadl;

public class WadlRuntimeException extends RuntimeException {

    public WadlRuntimeException(Exception e) {
        super(e);
    }

    public WadlRuntimeException(String message) {
        super(message);
    }

    public WadlRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
