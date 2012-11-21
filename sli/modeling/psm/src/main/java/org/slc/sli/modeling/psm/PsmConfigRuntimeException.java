package org.slc.sli.modeling.psm;

public class PsmConfigRuntimeException extends RuntimeException {

    public PsmConfigRuntimeException(Throwable cause) {
        super(cause);
    }

    public PsmConfigRuntimeException(String message) {
        super(message);
    }

    public PsmConfigRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
