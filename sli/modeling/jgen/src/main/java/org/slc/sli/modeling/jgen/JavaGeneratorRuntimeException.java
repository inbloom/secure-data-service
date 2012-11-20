package org.slc.sli.modeling.jgen;


/**
 * Model Java runtime exception.
 */
public class JavaGeneratorRuntimeException extends RuntimeException {

    public JavaGeneratorRuntimeException(Throwable cause) {
        super(cause);
    }

    public JavaGeneratorRuntimeException(String message) {
        super(message);
    }

    public JavaGeneratorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
