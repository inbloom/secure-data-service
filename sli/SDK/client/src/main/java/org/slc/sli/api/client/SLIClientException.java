package org.slc.sli.api.client;

/**
 * Generic exception used primarily by the SLIClient implementation.
 *
 * @author Stephan Altmueller
 *
 */
@SuppressWarnings("serial")
public class SLIClientException extends Exception {
    public SLIClientException() {
        super();
    }

    public SLIClientException(String msg) {
        super(msg);
    }
}
