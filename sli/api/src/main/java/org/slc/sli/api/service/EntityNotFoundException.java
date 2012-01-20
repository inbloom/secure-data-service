package org.slc.sli.api.service;

/**
 * Exception indicating a requested entity could not be found.
 * 
 */
public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7214099906661179616L;
    
    private String id;
    
    public EntityNotFoundException(String id) {
        super();
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
}
