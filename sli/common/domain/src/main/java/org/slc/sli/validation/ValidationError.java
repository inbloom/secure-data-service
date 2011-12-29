package org.slc.sli.validation;

import java.util.Arrays;

/**
 * Describes a validation rule violation.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class ValidationError {
    
    /**
     * Indicates the type of validation condition that was violated.
     * 
     * @author Ryan Farris <rfarris@wgen.net>
     * 
     */
    public enum ErrorType {
        INVALID_DATE_FORMAT, REQUIRED_FIELD_MISSING, UNKNOWN_FIELD, ENUMERATION_MISMATCH, INVALID_DATATYPE, REFERENTIAL_INFO_MISSING
        
    }
    
    final ErrorType type;
    final String fieldName;
    final Object fieldValue;
    final String[] expectedTypes;
    
    public ValidationError(ErrorType type, String fieldName, Object fieldValue, String[] expectedTypes) {
        this.type = type;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.expectedTypes = expectedTypes;
    }
    
    public ErrorType getType() {
        return type;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
    
    public String[] getExpectedTypes() {
        return expectedTypes;
    }
    
    @Override
    public String toString() {
        return "ValidationError [type=" + type + ", fieldName=" + fieldName + ", fieldValue=" + fieldValue
                + ", expectedTypes=" + Arrays.toString(expectedTypes) + "]";
    }
    
}
