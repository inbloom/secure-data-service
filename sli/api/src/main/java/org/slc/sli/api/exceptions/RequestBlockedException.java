package org.slc.sli.api.exceptions;

/**
 * Indicates that a request to the API has been blocked.
 */
public class RequestBlockedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RequestBlockedException(String message) {
        super(message);
    }
}
