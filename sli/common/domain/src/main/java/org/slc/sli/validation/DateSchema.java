package org.slc.sli.validation;

import java.util.List;

import org.slc.sli.validation.ValidationError.ErrorType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI Date Schema which validates date entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class DateSchema extends NeutralSchema {
    
    // Constructors
    public DateSchema() {
        this(NeutralSchemaType.DATE.getName());
    }
    
    public DateSchema(String xsdType) {
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
        boolean isValid = false;
        try {
            javax.xml.bind.DatatypeConverter.parseDate((String) entity);
            isValid = true;
        } catch (IllegalArgumentException e2) {
        }
        return addError(isValid, fieldName, entity, "RFC 3339", ErrorType.INVALID_DATE_FORMAT, errors);
    }
    
}
