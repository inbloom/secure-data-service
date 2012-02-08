package org.slc.sli.validation.schema;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.EntityValidationRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * SLI reference schema that validates entity references
 * @author srupasinghe
 *
 */
@Scope("prototype")
@Component
public class ReferenceSchema extends NeutralSchema {
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(ReferenceSchema.class);
    
    // Constructors
    public ReferenceSchema() {
        this(NeutralSchemaType.REFERENCE.getName());
    }
    
    public ReferenceSchema(String xsdType) {
        super(xsdType);
    }
    
    // Methods
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.REFERENCE;
    }

    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     * 
     * @param fieldName
     *            name of entity field being validated
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository           
     * @return true if valid
     */
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, EntityValidationRepository repo) {
        if (!addError(String.class.isInstance(entity), fieldName, entity, "String", ErrorType.INVALID_DATATYPE, errors)) {
            return false;
        }
        
        boolean found = true;
        
        try {
            //try to find an entity with the given id
            found = (repo.find(getAppInfo().getReferenceType(), (String) entity) != null);
        } catch (IllegalArgumentException iae) {
            //catch any malformed uuid exceptions
            found = false;
        }
        
        //if not found add the appropriate error
        if (!found) {
            LOG.debug("Broken reference in {}, {}, {}", new Object[]{entity, fieldName, errors});
            
            addError(false, fieldName, entity, "Valid reference", ErrorType.REFERENTIAL_INFO_MISSING, errors);
            return false;
        }
        
        return true;
    }
}
