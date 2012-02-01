package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map;

import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI Complex Schema which validates complex entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class ComplexSchema extends NeutralSchema {
    
    // Constructors
    public ComplexSchema() {
        this(NeutralSchemaType.COMPLEX.getName());
    }
    
    public ComplexSchema(String xsdType) {
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
        boolean isValid = true;
        
        if (entity instanceof Map) {
            Map<?, ?> entityMap = (Map<?, ?>) entity;
            for (String name : this.getFields().keySet()) {
                boolean fieldRequired = true;
                if (name.startsWith("*")) {
                    name = name.substring(1);
                    fieldRequired = false;
                }
                NeutralSchema fieldSchema = (NeutralSchema) this.getFields().get(name);
                
                Object fieldEntity = entityMap.get(name);
                if (fieldEntity == null) {
                    if (fieldRequired) {
                        return addError(false, fieldName, fieldEntity, "", ErrorType.REQUIRED_FIELD_MISSING, errors);
                    }
                } else {
                    boolean isFieldValid = fieldSchema.validate(name, fieldEntity, errors);
                    if (!isFieldValid) {
                        
                        // Not valid since field failed, but continue gathering further field
                        // validation info
                        isValid = false;
                        
                        // Return immediately since errors list was not indicated
                        if (errors == null) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return addError(false, fieldName, entity, "Complex Map", ErrorType.INVALID_DATATYPE, errors);
        }
        
        return isValid;
    }
    
}
