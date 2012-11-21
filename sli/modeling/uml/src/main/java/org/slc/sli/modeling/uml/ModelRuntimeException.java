package org.slc.sli.modeling.uml;

/**
 * UML exception.
 */
public class ModelRuntimeException extends RuntimeException {

    public ModelRuntimeException(Throwable cause) {
        super(cause);
    }

    public ModelRuntimeException(String message) {
        super(message);
    }

    public ModelRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
