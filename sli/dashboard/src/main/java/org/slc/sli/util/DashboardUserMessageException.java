package org.slc.sli.util;

/**
 * Basic user-friendly exception
 * @author agrebneva
 *
 */
public class DashboardUserMessageException extends RuntimeException {
    private static final long serialVersionUID = -5302373303075039727L;
    public DashboardUserMessageException(String message) {
        super(message);
    }
}
