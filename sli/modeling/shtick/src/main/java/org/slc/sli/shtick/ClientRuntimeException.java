package org.slc.sli.shtick;

public class ClientRuntimeException extends RuntimeException {

    public ClientRuntimeException(Throwable cause) {
        super(cause);
    }

    public ClientRuntimeException(String message) {
        super(message);
    }

    public ClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
