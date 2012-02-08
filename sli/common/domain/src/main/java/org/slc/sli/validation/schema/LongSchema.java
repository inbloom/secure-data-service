package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.EntityValidationRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * 
 * SLI Long Schema which validates long entities
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Scope("prototype")
@Component
public class LongSchema extends NeutralSchema {
    
    // Constructors
    public LongSchema() {
        this(NeutralSchemaType.LONG.getName());
    }
    
    public LongSchema(String xsdType) {
        super(xsdType);
    }
    
    // Methods
    
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.LONG;
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
        Long data = NumberUtils.toLong(entity);
        if (!addError(data != null, fieldName, entity, "Long", ErrorType.INVALID_DATATYPE, errors)) {
            return false;
        }
        
        if (this.getProperties() != null) {
            for (Entry<String, Object> entry : this.getProperties().entrySet()) {
                if (Restriction.isRestriction(entry.getKey())) {
                    long restrictionValue = Long.parseLong(entry.getValue().toString());
                    switch (Restriction.fromValue(entry.getKey())) {
                    case MIN_INCLUSIVE:
                        if (!addError(data.compareTo(restrictionValue) >= 0, fieldName, entity, "min="
                                + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case MAX_INCLUSIVE:
                        if (!addError(data.compareTo(restrictionValue) <= 0, fieldName, entity, "max="
                                + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case MIN_EXCLUSIVE:
                        if (!addError(data.compareTo(restrictionValue) > 0, fieldName, entity, "min-exclusive="
                                + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case MAX_EXCLUSIVE:
                        if (!addError(data.compareTo(restrictionValue) < 0, fieldName, entity, "max-exclusive="
                                + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case TOTAL_DIGITS:
                        if (!addError(NumberUtils.totalDigits(data) <= restrictionValue, fieldName, entity,
                                "total-digits=" + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }
    
}
