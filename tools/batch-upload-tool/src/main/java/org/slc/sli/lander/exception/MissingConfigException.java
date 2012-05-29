package org.slc.sli.lander.exception;

public class MissingConfigException extends Exception {
    private final String fieldName;
    
    public MissingConfigException(String fieldName) {
        super();
        this.fieldName = fieldName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
}
