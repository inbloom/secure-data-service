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
 * SLI Integer Schema which validates integer entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class IntegerSchema extends NeutralSchema {

    // Constructors
    public IntegerSchema() {
        this(NeutralSchemaType.INTEGER.getName());
    }

    public IntegerSchema(String xsdType) {
        super(xsdType);
    }

    // Methods

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.INTEGER;
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
        Integer data = NumberUtils.toInteger(entity);
        if (!addError(data != null, fieldName, entity, "Integer", ErrorType.INVALID_DATATYPE, errors)) {
            return false;
        }

        if (this.getProperties() != null) {
            for (Entry<String, Object> entry : this.getProperties().entrySet()) {
                if (Restriction.isRestriction(entry.getKey())) {
                    int restrictionValue = Integer.parseInt(entry.getValue().toString());
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
