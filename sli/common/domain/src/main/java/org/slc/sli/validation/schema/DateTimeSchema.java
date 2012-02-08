package org.slc.sli.validation.schema;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.EntityValidationRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * 
 * SLI Date Schema which validates datetime entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class DateTimeSchema extends NeutralSchema {
    
    // Constructors
    public DateTimeSchema() {
        this(NeutralSchemaType.DATETIME.getName());
    }
    
    public DateTimeSchema(String xsdType) {
        super(xsdType);
    }
    
    // Methods
    
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.DATETIME;
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
        boolean isValid;
        try {
            javax.xml.bind.DatatypeConverter.parseDateTime((String) entity);
            isValid = true;
        } catch (IllegalArgumentException e2) {
            isValid = false;
        }
        return addError(isValid, fieldName, entity, "RFC 3339 DateTime", ErrorType.INVALID_DATE_FORMAT, errors);
    }
    
}
