package org.slc.sli.shtick;

public class ClientRuntimeException extends RuntimeException {

    public ClientRuntimeException(Exception e) {
        super(e);
    }

    public ClientRuntimeException(String message) {
        super(message);
    }

    public ClientRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
