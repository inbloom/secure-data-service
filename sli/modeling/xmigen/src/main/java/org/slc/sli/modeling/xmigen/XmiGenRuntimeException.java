package org.slc.sli.modeling.xmigen;

public class XmiGenRuntimeException extends RuntimeException {

    public XmiGenRuntimeException(Throwable cause) {
        super(cause);
    }

    public XmiGenRuntimeException(String message) {
        super(message);
    }

    public XmiGenRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
