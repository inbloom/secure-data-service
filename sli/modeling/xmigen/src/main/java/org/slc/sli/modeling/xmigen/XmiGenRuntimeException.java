package org.slc.sli.modeling.xmigen;

public class XmiGenRuntimeException extends RuntimeException {

    public XmiGenRuntimeException(Exception e) {
        super(e);
    }

    public XmiGenRuntimeException(String message) {
        super(message);
    }

    public XmiGenRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
