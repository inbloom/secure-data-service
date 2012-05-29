package org.slc.sli.validation.schema;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Repository;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 *
 * SLI Boolean Schema which validates boolean entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class BooleanSchema extends NeutralSchema {

    // Constructors
    public BooleanSchema() {
        this(NeutralSchemaType.BOOLEAN.getName());
    }

    public BooleanSchema(String xsdType) {
        super(xsdType);
    }

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.BOOLEAN;
    }

    @Override
    public Object convert(Object value) {
        return NumberUtils.converterHelper(value, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                String stringValue = (String) value;
                stringValue = stringValue.toLowerCase();
                if (stringValue.equals("true")) {
                    return Boolean.TRUE;
                } else if (stringValue.equals("false")) {
                    return Boolean.FALSE;
                } else {
                    throw new NumberFormatException(value + " is not a boolean");
                }
            }
        });
    }
    
    // Methods

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
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        entity = convert(entity);
        return addError(Boolean.class.isInstance(entity), fieldName, entity, "Boolean", ErrorType.INVALID_DATATYPE,
                errors);
    }

}
