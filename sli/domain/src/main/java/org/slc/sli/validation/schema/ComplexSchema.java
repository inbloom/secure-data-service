/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.validation.schema;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
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
     * @param validateFieldName
     *            name of entity field being validated. Not used by this class
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    @Override
    protected boolean validate(String validateFieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        return this.validate(validateFieldName, entity, errors, repo, true);
    }

    private boolean validate(String validateFieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo, boolean checkForMissingFields) {
        boolean isValid = true;

        if (entity instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> entityMap = (Map<String, ?>) entity;

            if (checkForMissingFields) {
             // Make sure the entity contains all required fields
                for (Map.Entry<String, NeutralSchema> entry : getFields().entrySet()) {
                    NeutralSchema schema = entry.getValue();
                    AppInfo appInfo = schema.getAppInfo();
                    if (appInfo != null && appInfo.isRequired()) {
                        Object element = entityMap.get(entry.getKey());
                        if (element == null) {
                            isValid = false;
                        } else if (element instanceof Collection) {
                            if (((Collection<?>) element).isEmpty()) {
                                isValid = false;
                            }
                        } else if (element instanceof Map) {
                            if (((Map<?, ?>) element).isEmpty()) {
                                isValid = false;
                            }
                        }

                        if (!isValid) {
                            addError(false, entry.getKey(), "", schema.getSchemaType().toString(),
                                    ErrorType.REQUIRED_FIELD_MISSING,
                                    errors);
                        }
                    }
                }
            }

            for (Map.Entry<String, ?> entry : entityMap.entrySet()) {
                String fieldName = entry.getKey();
                Object rawFieldValue = entry.getValue();

                NeutralSchema fieldSchema = getFields().get(fieldName);
                if (fieldSchema == null) {
                    return addError(false, fieldName, rawFieldValue, "", ErrorType.UNKNOWN_FIELD, errors);
                }

                AppInfo appInfo = fieldSchema.getAppInfo();
                if (rawFieldValue == null) {
                    if (appInfo != null && appInfo.isRequired() && checkForMissingFields) {
                        return addError(false, fieldName, null, "", ErrorType.REQUIRED_FIELD_MISSING, errors);
                    }
                } else {
                    //validate raw field data against field's schema
                    if (fieldSchema.validate(fieldName, rawFieldValue, errors, repo)) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> myEntityMap = (Map<String, Object>) entityMap;
                        Object convertedFieldValue = fieldSchema.convert(rawFieldValue);
                        myEntityMap.put(fieldName, convertedFieldValue);
                    } else {
                        isValid = false;

                        // Return immediately since errors list was not indicated
                        if (errors == null) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return addError(false, validateFieldName, entity, "Complex Map", ErrorType.INVALID_DATATYPE, errors);
        }

        return isValid;
    }

    /**
     * Validates only the present fields.
     *
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    @Override
    public boolean validatePresent(Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        return this.validate("", entity, errors, repo, false);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

}
