package org.slc.sli.modeling.waudit;

public class WadlAuditRuntimeException extends RuntimeException {

    public WadlAuditRuntimeException(Exception e) {
        super(e);
    }

    public WadlAuditRuntimeException(String message) {
        super(message);
    }

    public WadlAuditRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
