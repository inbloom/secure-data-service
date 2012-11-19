package org.slc.sli.modeling.jgen;


public class JavaGeneratorRuntimeException extends RuntimeException {

    public JavaGeneratorRuntimeException(Exception e) {
        super(e);
    }

    public JavaGeneratorRuntimeException(String message) {
        super(message);
    }

    public JavaGeneratorRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
