package org.slc.sli.validation.schema;

import java.util.List;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * A schema that holds a primitive, such as a Double, Integer, Long, or Boolean.
 * 
 * @author kmyers
 *
 */
public abstract class PrimitiveSchema extends NeutralSchema {
    
    private NeutralSchemaType neutralSchemaType;

    public PrimitiveSchema(NeutralSchemaType neutralSchemaType) {
        super(neutralSchemaType.getName());
        this.neutralSchemaType = neutralSchemaType;
    }

    // Methods

    @Override
    public NeutralSchemaType getSchemaType() {
        return this.neutralSchemaType;
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
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        
        //attempt input conversion. Only type is confirmed
        try {
            entity = convert(entity);
        } catch (IllegalArgumentException iae) {
            super.addError(fieldName, entity, this.neutralSchemaType.getName(), ErrorType.INVALID_DATATYPE, errors);
            return false;
        }
        
        return true;
    }
}
