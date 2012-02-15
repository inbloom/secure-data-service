package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slc.sli.domain.EntityRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;

/**
 * Schema which validates choices.
 * 
 * A choice can have minOccurs and maxOccurs attributes. These indicate how many of the
 * elements are allowed. Default is 1 -- only one element is allowed.
 * 
 * A choice element can have minOccurs and maxOccurs attributes. This indicates how many times
 * the same element is allowed. maxOccurs can also be set to UNBOUNDED.
 * 
 * @author asaarela
 * @author nbrown
 * 
 */
public class ChoiceSchema extends NeutralSchema {
    
    private static final String MAX_OCCURS = "maxOccurs";
    private static final String MIN_OCCURS = "minOccurs";
    
    // Match the Apache XMLSchema type for unbounded values.
    public static final long UNBOUNDED = Long.MAX_VALUE;
    
    public ChoiceSchema(long minOccurs, long maxOccurs) {
        super(NeutralSchemaType.CHOICE.getName());
        
        getProperties().put(MIN_OCCURS, minOccurs);
        getProperties().put(MAX_OCCURS, maxOccurs);
    }
    
    public ChoiceSchema(String name) {
        super(name);
    }
    
    public void setMinOccurs(long i) {
        getProperties().put(MIN_OCCURS, i);
    }
    
    public void setMaxOccurs(long i) {
        getProperties().put(MAX_OCCURS, i);
    }
    
    public long getMinOccurs() {
        return (Long) getProperties().get(MIN_OCCURS);
    }
    
    public long getMaxOccurs() {
        return (Long) getProperties().get(MAX_OCCURS);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, EntityRepository repo) {
        
        if (getMinOccurs() < 2 && getMaxOccurs() == 1 && entity instanceof Map) {
            Map<String, Object> datum = (Map<String, Object>) entity;
            if (datum.size() > 1) {
                return false;
            }
            Entry<String, Object> datumValue = datum.entrySet().iterator().next();
            NeutralSchema fieldSchema = this.getFields().get(datumValue.getKey());
            if (fieldSchema == null) {
                return false;
            }
            if (fieldSchema.validate(datumValue.getValue())) {
                return true;
            }
        } else if (getMaxOccurs() > 1) {
            if (!(entity instanceof List)) {
                return false;
            }
            List<Map<String, Object>> data = (List<Map<String, Object>>) entity;
            if (data.size() < getMinOccurs()) {
                return false;
            } else if (data.size() > getMaxOccurs()) {
                return false;
            }
            for (Map<String, Object> datum : data) {
                if (datum.size() > 1) {
                    return false;
                }
                Entry<String, Object> datumValue = datum.entrySet().iterator().next();
                NeutralSchema fieldSchema = this.getFields().get(datumValue.getKey());
                if (fieldSchema == null) {
                    return false;
                }
                if (!fieldSchema.validate(datumValue.getValue())) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.CHOICE;
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public boolean isSimple() {
        return false;
    }
    
}
