package org.slc.sli.api.security.resolve;

/**
 * Realm role mapping exception 
 * 
 * @author jnanney
 *
 */
public class RealmRoleMappingException extends Exception {

    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a realm role mapping exception with the given message. 
     * 
     * @param message - the detailed exception message
     */
    public RealmRoleMappingException(String message) {
        super(message);
    }

}
