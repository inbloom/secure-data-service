package org.slc.sli.validation;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class EntityValidationException extends RuntimeException {
    
    private static final long serialVersionUID = 5579596873501684518L;
    
    final List<ValidationError> errors;
    final String entityId;
    final String entityType;
    
    protected EntityValidationException(String entityId, String entityType, List<ValidationError> errors) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.errors = Collections.unmodifiableList(errors);
    }
    
    public List<ValidationError> getValidationErrors() {
        return errors;
    }
    
    public String getEntityId() {
        return entityId;
    }
    
    public String getEntityType() {
        return entityType;
    }
}
