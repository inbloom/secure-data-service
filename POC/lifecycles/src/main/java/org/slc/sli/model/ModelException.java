package org.slc.sli.model;

/**
 * Model exception.
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */

public class ModelException extends Exception {
    
    // Constructors
    public ModelException() {
    }
    
    public ModelException(String message) {
        super(message);
    }
    
    public ModelException(Throwable cause) {
        super(cause);
    }
    
    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
