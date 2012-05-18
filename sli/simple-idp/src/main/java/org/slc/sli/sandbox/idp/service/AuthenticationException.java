package org.slc.sli.sandbox.idp.service;

/**
 * Exception thrown when authentication fails.
 * 
 * @author scole
 * 
 */
public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public AuthenticationException(Exception e) {
        super(e);
    }
    
    public AuthenticationException(String msg) {
        super(msg);
    }
}
