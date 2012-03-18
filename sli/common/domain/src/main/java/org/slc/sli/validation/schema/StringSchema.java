package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.EntityRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 *
 * SLI String Schema which validates string entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class StringSchema extends NeutralSchema {

    // Constructors
    public StringSchema() {
        this(NeutralSchemaType.STRING.getName());
    }

    public StringSchema(String xsdType) {
        super(xsdType);
    }

    // Methods

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.STRING;
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
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, EntityRepository repo) {
        if (!addError(String.class.isInstance(entity), fieldName, entity, "String", ErrorType.INVALID_DATATYPE, errors)) {
            return false;
        }
        String data = (String) entity;
        if (this.getProperties() != null) {
            for (Entry<String, Object> entry : this.getProperties().entrySet()) {
                if (Restriction.isRestriction(entry.getKey())) {
                    String restrictionValue = (String) entry.getValue();
                    switch (Restriction.fromValue(entry.getKey())) {
                    case PATTERN:
                        if (!addError(data.matches(restrictionValue), fieldName, entity, "pattern=" + restrictionValue,
                                ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case LENGTH:
                        if (!addError(data.length() == Integer.parseInt(restrictionValue), fieldName, entity, "length="
                                + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case MIN_LENGTH:
                        if (!addError(data.length() >= Integer.parseInt(restrictionValue), fieldName, entity,
                                "min-length=" + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                            return false;
                        }
                        break;
                    case MAX_LENGTH:
                        if (!addError(data.length() <= Integer.parseInt(restrictionValue), fieldName, entity,
                                "max-length=" + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
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
