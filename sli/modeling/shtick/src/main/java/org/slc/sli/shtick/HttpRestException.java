package org.slc.sli.shtick;

/**
 * @author jstokes
 */
public class HttpRestException extends Exception {

    private int statusCode;

    public HttpRestException(int statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public HttpRestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
