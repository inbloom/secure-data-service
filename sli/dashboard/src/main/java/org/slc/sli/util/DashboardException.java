package org.slc.sli.util;

/**
 * Basic Exception with internal information that is not user-friendly
 * @author agrebneva
 *
 */
public class DashboardException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public DashboardException(String message) {
        super(message);
    }
}
