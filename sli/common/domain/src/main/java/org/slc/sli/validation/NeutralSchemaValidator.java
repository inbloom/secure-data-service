package org.slc.sli.validation;

import java.util.LinkedList;
import java.util.List;

import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validates an Entity body against the appropriate SLI neutral schema.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * @author Ryan Farris <rfarris@wgen.net>
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Component
public class NeutralSchemaValidator implements EntityValidator {
    
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(NeutralSchemaValidator.class);
    
    // Attributes
    @Autowired
    private NeutralSchemaRegistry entitySchemaRegistry;
    
    // Constructors
    public NeutralSchemaValidator() {
        
    }
    
    // Methods
    
    /**
     * Validates the given entity using its SLI Neutral Schema.
     */
    public boolean validate(Entity entity) throws EntityValidationException {
        
        NeutralSchema schema = entitySchemaRegistry.findSchemaForType(entity);
        if (schema == null) {
            throw new RuntimeException("No schema associated for type: " + entity.getType());
        }
        
        List<ValidationError> errors = new LinkedList<ValidationError>();
        boolean valid = schema.validate("", entity.getBody(), errors);
        if (!valid) {
            throw new EntityValidationException(entity.getEntityId(), entity.getType(), errors);
        }
        
        return true;
    }
    
    public void setSchemaRegistry(NeutralSchemaRegistry schemaRegistry) {
        this.entitySchemaRegistry = schemaRegistry;
    }
    
}
