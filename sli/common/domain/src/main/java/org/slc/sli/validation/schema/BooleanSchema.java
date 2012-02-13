package org.slc.sli.validation.schema;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.EntityRepository;
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
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, EntityRepository repo) {
        return addError(Boolean.class.isInstance(entity), fieldName, entity, "Boolean", ErrorType.INVALID_DATATYPE,
                errors);
    }

}
