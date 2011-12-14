package org.slc.sli.validation;

/**
 * Runtime exception when validation error occur.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public class EntityValidationException extends RuntimeException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5895977217174135745L;
    
    // TODO - this should probably be an enum
    public static final int NO_ASSOCIATED_SCHEMA = 1;
    
    /**
     * The status code.
     */
    private int statusCode;
    
    /**
     * Construct a new validation exception
     * 
     * @param statusCode
     * @param message
     */
    public EntityValidationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    
}
