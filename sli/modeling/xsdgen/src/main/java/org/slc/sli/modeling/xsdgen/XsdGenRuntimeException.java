package org.slc.sli.modeling.xsdgen;

public class XsdGenRuntimeException extends RuntimeException {

    public XsdGenRuntimeException(Exception e) {
        super(e);
    }

    public XsdGenRuntimeException(String message) {
        super(message);
    }

    public XsdGenRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
