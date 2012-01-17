package org.slc.sli.validation;

import java.math.BigDecimal;
import java.util.List;

import org.slc.sli.validation.ValidationError.ErrorType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI Decimal Schema which validates decimal entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class DecimalSchema extends NeutralSchema {
    
    // Constructors
    public DecimalSchema() {
        this(NeutralSchemaType.DECIMAL.getName());
    }
    
    public DecimalSchema(String xsdType) {
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
        return addError(BigDecimal.class.isInstance(entity), fieldName, entity, "Decimal", ErrorType.INVALID_DATATYPE,
                errors);
    }
    
}
