package org.slc.sli.shtick;

/**
 * @author jstokes
 */
public final class StatusCodeException extends Exception {
    private static final long serialVersionUID = 1L;

    private int statusCode;

    public StatusCodeException(final int statusCode) {
        this(statusCode, null);
    }

    public StatusCodeException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
