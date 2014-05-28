package org.slc.sli.validation.schema;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * SLI Date Schema which validates date entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 */
@Scope("prototype")
@Component
public class DateSchema extends NeutralSchema {
    private static final Logger LOG = LoggerFactory.getLogger(DateSchema.class);

    public DateSchema() {
        this(NeutralSchemaType.DATE.getName());
    }

    public DateSchema(String xsdType) {
        super(xsdType);
    }

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.DATE;
    }

    @Override
    public Object convert(Object value) {
        try {
            DatatypeConverter.parseDate((String) value);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.error("Failed to parse date", ex);
            throw new EntityValidationException(ex, "irrelevant", "doesn't matter", new ArrayList<ValidationError>());
        }
        return value;
    }

    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * @param fieldName name of entity field being validated
     * @param entity    being validated using this SLI Schema
     * @param errors    list of current errors
     * @param repo      reference to the entity repository                                                                                                                      git status
     * @return true if valid
     */
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        boolean isValid;
        try {
            javax.xml.bind.DatatypeConverter.parseDate((String) entity);
            isValid = true;
        } catch (IllegalArgumentException e2) {
            isValid = false;
        }
        return addError(isValid, fieldName, entity, "RFC 3339 Date", ErrorType.INVALID_DATE_FORMAT, errors);
    }

}
