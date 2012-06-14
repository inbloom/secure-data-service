package org.slc.sli.shtick;

/**
 * @author jstokes
 */
public final class RestException extends Exception {
    private static final long serialVersionUID = 1L;

    private int statusCode;

    public RestException(final int statusCode) {
        this(statusCode, null);
    }

    public RestException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
