package org.slc.sli.validation.schema;

import java.util.List;

import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

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
    private static final long MAX_DEFAULT = 1;
    private static final long MIN_DEFAULT = 0;
    
    // Match the Apache XMLSchema type for unbounded values.
    public static final long UNBOUNDED = Long.MAX_VALUE;
    private List<NeutralSchema> choices;
    
    public ChoiceSchema(long minOccurs, long maxOccurs, List<NeutralSchema> choices) {
        super(NeutralSchemaType.CHOICE.getName());
        
        getProperties().put(MIN_OCCURS, minOccurs);
        getProperties().put(MAX_OCCURS, maxOccurs);
        
        this.choices = choices;
    }
    
    public ChoiceSchema(String name) {
        super(name);
    }
    
    public void setMinChoices(long i) {
        getProperties().put(MIN_OCCURS, i);
    }
    
    public long getMinChoices() {
        Long i = (Long) getProperties().get(MIN_OCCURS);
        if (i == null) {
            return MIN_DEFAULT;
        }
        return i.longValue();
    }
    
    public void setMaxChoices(long i) {
        getProperties().put(MAX_OCCURS, i);
    }
    
    public long getMaxChoices() {
        Long i = (Long) getProperties().get(MAX_OCCURS);
        if (i == null) {
            return MAX_DEFAULT;
        }
        return i.longValue();
    }
    
    public void setChoices(List<NeutralSchema> choices) {
        this.choices = choices;
    }
    
    public List<NeutralSchema> getChoices() {
        return choices;
    }
    
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors) {
        
        boolean isValid = true;
        
        if (entity instanceof List) {
            
            @SuppressWarnings("unchecked")
            List<Object> choiceList = (List<Object>) entity;
            
            int choiceCount = 0;

            boolean foundValid = false;
            for (int i = 0; i < choiceList.size(); ++i) {
                
                Object choiceEntry = choiceList.get(i);
                
                for (NeutralSchema itemSchema : getChoices()) {
                    if (itemSchema.validate(choiceEntry)) {
                        choiceCount++;
                        foundValid = true;
                        break;
                    }
                }
            }
            
            if (choiceCount > 0 && !foundValid) {
                return addError(false, fieldName, entity, "Choice", ErrorType.INVALID_VALUE_FOR_CHOICE, errors);
            }
            
            if (getMinChoices() != 0 && choiceCount < getMinChoices()) {
                return addError(false, fieldName, entity, "Choice", ErrorType.TOO_FEW_CHOICES, errors);
            }
            
            if (getMaxChoices() != UNBOUNDED && choiceCount > getMaxChoices()) {
                return addError(false, fieldName, entity, "Choice", ErrorType.TOO_MANY_CHOICES, errors);
            }
            
        } else {
            if (getMinChoices() != 0) {
                return addError(false, fieldName, entity, "Complex Map", ErrorType.INVALID_DATATYPE, errors);
            }
        }
        
        return isValid;
    }
    
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.CHOICE;
    }
    
}
