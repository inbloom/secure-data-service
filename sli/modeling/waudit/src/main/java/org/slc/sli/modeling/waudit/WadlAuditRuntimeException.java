package org.slc.sli.modeling.waudit;

public class WadlAuditRuntimeException extends RuntimeException {

    public WadlAuditRuntimeException(Throwable cause) {
        super(cause);
    }

    public WadlAuditRuntimeException(String message) {
        super(message);
    }

    public WadlAuditRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
