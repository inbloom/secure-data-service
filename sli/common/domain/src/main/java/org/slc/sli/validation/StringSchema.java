package org.slc.sli.validation;

import java.util.List;

import org.slc.sli.validation.ValidationError.ErrorType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI String Schema which validates string entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class StringSchema extends NeutralSchema {
    
    // Constructors
    public StringSchema() {
        this(NeutralSchemaType.STRING.getName());
    }
    
    public StringSchema(String xsdType) {
        super(xsdType);
    }
    
    // Methods
    
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
     * @return true if valid
     */
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors) {
        return addError(String.class.isInstance(entity), fieldName, entity, "String", ErrorType.INVALID_DATATYPE,
                errors);
    }
    
}
