package org.slc.sli.shtick;

/**
 * @author jstokes
 */
public class HttpRestException extends Exception {
    public HttpRestException(String message) {
        super(message);
    }

    public HttpRestException() {
        super();
    }
}
