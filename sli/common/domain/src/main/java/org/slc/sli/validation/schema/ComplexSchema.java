package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.EntityValidationRepository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 *
 * SLI Complex Schema which validates complex entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class ComplexSchema extends NeutralSchema {

    // Constructors
    public ComplexSchema() {
        this(NeutralSchemaType.COMPLEX.getName());
    }

    public ComplexSchema(String xsdType) {
        super(xsdType);
    }

    // Methods
    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.COMPLEX;
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
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, EntityValidationRepository repo) {
        boolean isValid = true;

        if (entity instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> entityMap = (Map<String, ?>) entity;

            // Make sure the entity contains all required fields
            for (Map.Entry<String, NeutralSchema> entry : getFields().entrySet()) {
                NeutralSchema schema = entry.getValue();
                AppInfo appInfo = schema.getAppInfo();
                if (appInfo != null && appInfo.isRequired() && !entityMap.containsKey(entry.getKey())) {
                    addError(false, entry.getKey(), "", schema.getSchemaType().toString(),
                            ErrorType.REQUIRED_FIELD_MISSING,
                            errors);
                    isValid = false;
                }
            }

            for (String name : entityMap.keySet()) {
                Object fieldEntity = entityMap.get(name);

                NeutralSchema fieldSchema = getFields().get(name);
                if (fieldSchema == null) {
                    return addError(false, name, fieldEntity, "", ErrorType.UNKNOWN_FIELD, errors);
                }

                AppInfo appInfo = fieldSchema.getAppInfo();
                if (fieldEntity == null) {
                    if (appInfo != null && appInfo.isRequired()) {
                        return addError(false, name, fieldEntity, "", ErrorType.REQUIRED_FIELD_MISSING, errors);
                    }
                } else {
                    boolean isFieldValid = fieldSchema.validate(name, fieldEntity, errors, repo);
                    if (!isFieldValid) {

                        // Not valid since field failed, but continue gathering further field
                        // validation info
                        isValid = false;

                        // Return immediately since errors list was not indicated
                        if (errors == null) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return addError(false, fieldName, entity, "Complex Map", ErrorType.INVALID_DATATYPE, errors);
        }

        return isValid;
    }

}
