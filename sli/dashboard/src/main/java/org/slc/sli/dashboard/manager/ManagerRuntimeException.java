package org.slc.sli.dashboard.manager;

public class ManagerRuntimeException extends RuntimeException {

    public ManagerRuntimeException(Throwable cause) {
        super(cause);
    }

    public ManagerRuntimeException(String message) {
        super(message);
    }

    public ManagerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
