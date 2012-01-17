package org.slc.sli.validation;

import java.util.List;

import org.slc.sli.validation.ValidationError.ErrorType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI Restricted String Schema which validates string entities with certain restrictions attached
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class RestrictedSchema extends NeutralSchema {
    
    // Constants
    public static final String PATTERN = "pattern";
    public static final String LENGTH = "length";
    public static final String MIN_LENGTH = "min-length";
    public static final String MAX_LENGTH = "max-length";
    public static final String MIN_INCLUSIVE = "min";
    public static final String MAX_INCLUSIVE = "max";
    public static final String MIN_EXCLUSIVE = "min-exclusive";
    public static final String MAX_EXCLUSIVE = "max-exclusive";
    public static final String TOTAL_DIGITS = "total-digits";
    public static final String FRACTION_DIGITS = "fraction-digits";
    public static final String WHITE_SPACE = "white-space";
    
    // Constructors
    public RestrictedSchema() {
        this(NeutralSchemaType.RESTRICTED.getName());
    }
    
    public RestrictedSchema(String xsdType) {
        super(xsdType);
    }
    
    // Methods
    public boolean isPrimitive() {
        return false;
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
     * @return true if valid
     */
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors) {
        
        // TODO - implement validation leveraging all possible restrictions present in Schema
        // properties
        
        return addError(matchesRestrictions(entity), fieldName, entity, "Restricted", ErrorType.INVALID_DATATYPE,
                errors);
    }
    
    protected boolean matchesRestrictions(Object entity) {
        if (String.class.isInstance(entity)) {
            String restrictedEntity = (String) entity;
            if (this.getProperties() != null) {
                for (String key : this.getProperties().keySet()) {
                    String value = (String) this.getProperties().get(key);
                    Integer numericValue = Integer.parseInt(value);
                    if (key.equals(LENGTH)) {
                        if (restrictedEntity.length() != numericValue)
                            return false;
                    }
                    if (key.equals(MIN_LENGTH)) {
                        if (restrictedEntity.length() < numericValue)
                            return false;
                    }
                    if (key.equals(MAX_LENGTH)) {
                        if (restrictedEntity.length() > numericValue)
                            return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
}
