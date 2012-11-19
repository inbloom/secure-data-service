package org.slc.sli.modeling.uml;

public class ModelRuntimeException extends RuntimeException {

    public ModelRuntimeException(Exception e) {
        super(e);
    }

    public ModelRuntimeException(String message) {
        super(message);
    }

    public ModelRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
