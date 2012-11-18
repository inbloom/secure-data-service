package org.slc.sli.modeling.psm;

public class PsmConfigRuntimeException extends RuntimeException {

    public PsmConfigRuntimeException(Exception e) {
        super(e);
    }

    public PsmConfigRuntimeException(String message) {
        super(message);
    }

    public PsmConfigRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
