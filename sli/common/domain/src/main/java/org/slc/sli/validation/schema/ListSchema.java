package org.slc.sli.validation.schema;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.EntityRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.slc.sli.validation.schema.Annotation.AnnotationType;

/**
 * 
 * SLI List Schema which validates a list or collection of entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class ListSchema extends NeutralSchema {
    
    // Attributes
    private List<NeutralSchema> list = new LinkedList<NeutralSchema>();
    
    // Constructors
    public ListSchema() {
        this(NeutralSchemaType.LIST.getName());
    }
    
    public ListSchema(String xsdType) {
        super(xsdType);
    }
    
    // Methods
    
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.LIST;
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    public void setList(List<NeutralSchema> list) {
        this.list = list;
    }
    
    public List<NeutralSchema> getList() {
        return list;
    }
    
    @Override
    public boolean isSimple() {
        return false;
    }

    /**
     * This is a temp hack to fix a bug.
     * Annotations do not inherit from list to it's members
     * The solution to this problem is to remove the whole concept of multiple schema types for a
     * list, which is now handled by ChoiceSchema
     */
    public void updateAnnotations() {
        AppInfo info = (AppInfo) annotations.get(AnnotationType.APPINFO);
        if (info != null) {
            for (NeutralSchema itemSchema : getList()) {
                itemSchema.inheritAnnotations(info);
            }
        }
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
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, EntityRepository repo) {
        boolean isValid = true;
        
        if (entity instanceof List) {
            List<?> entityList = (List<?>) entity;
            for (Object fieldEntity : entityList) {
                
                // Allow validation according to ANY item Schemas in the ListSchema list (xs:choice
                // scenario)
                boolean isFieldValid = false;
                for (NeutralSchema itemSchema : getList()) {
                    
                    // Choice scenario will not provide validation errors (null)
                    if (itemSchema.validate(fieldName, fieldEntity, errors, repo)) {
                        isFieldValid = true;
                        break;
                    }
                }
                
                if (!isFieldValid) {
                    isValid = false;
                    if (errors == null) {
                        return false;
                    }
                }
            }
            
            if (getProperties() != null) {
                for (Entry<String, Object> entry : getProperties().entrySet()) {
                    if (Restriction.isRestriction(entry.getKey())) {
                        long restrictionValue = Long.parseLong(entry.getValue().toString());
                        switch (Restriction.fromValue(entry.getKey())) {
                            case LENGTH:
                                if (!addError(entityList.size() == restrictionValue, fieldName, entity, "length="
                                        + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                                    return false;
                                }
                                break;
                            case MIN_LENGTH:
                                if (!addError(entityList.size() >= restrictionValue, fieldName, entity, "min-length="
                                        + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                                    return false;
                                }
                                break;
                            case MAX_LENGTH:
                                if (!addError(entityList.size() <= restrictionValue, fieldName, entity, "max-length="
                                        + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                                    return false;
                                }
                                break;
                        }
                    }
                }
            }
            
        } else {
            return addError(false, fieldName, entity, "List", ErrorType.INVALID_DATATYPE, errors);
        }
        
        return isValid;
    }
    
}
