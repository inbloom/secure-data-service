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

import java.util.List;
import java.util.Map.Entry;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * A schema that converts input to a format of number. The exact format is generic.
 * 
 * @author kmyers
 *
 * @param <T> Numeric type that this will cast data to
 */
public abstract class PrimitiveNumericSchema<T extends Comparable<T>> extends PrimitiveSchema {
    
    public PrimitiveNumericSchema(NeutralSchemaType neutralSchemaType) {
        super(neutralSchemaType);
    }

    // Methods
    
    protected boolean validateAgainstRestrictions(String fieldName, Comparable<T> entity, List<ValidationError> errors, 
            Repository<Entity> repo) {
        if (super.getProperties() != null) {
            for (Entry<String, Object> entry : super.getProperties().entrySet()) {
                if (Restriction.isRestriction(entry.getKey())) {
                    @SuppressWarnings("unchecked")
                    T restrictionValue = (T) convert(entry.getValue().toString());

                    switch (Restriction.fromValue(entry.getKey())) {
                        case MIN_INCLUSIVE:
                            if (!addError(entity.compareTo(restrictionValue) >= 0,
                                    fieldName, entity, "min=" + restrictionValue,
                                    ErrorType.INVALID_VALUE, errors)) {
                                return false;
                            }
                            break;
                        case MAX_INCLUSIVE:
                            if (!addError(entity.compareTo(restrictionValue) <= 0,
                                    fieldName, entity, "max=" + restrictionValue,
                                    ErrorType.INVALID_VALUE, errors)) {
                                return false;
                            }
                            break;
                        case MIN_EXCLUSIVE:
                            if (!addError(entity.compareTo(restrictionValue) > 0,
                                    fieldName, entity, "min-exclusive="
                                            + restrictionValue,
                                    ErrorType.INVALID_VALUE, errors)) {
                                return false;
                            }
                            break;
                        case MAX_EXCLUSIVE:
                            if (!addError(entity.compareTo(restrictionValue) < 0,
                                    fieldName, entity, "max-exclusive="
                                            + restrictionValue,
                                    ErrorType.INVALID_VALUE, errors)) {
                                return false;
                            }
                            break;
                    }
                }
            }
        }

        return true;
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
    @SuppressWarnings("unchecked")
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        
        //attempt input conversion. Only type is confirmed
        try {
            Object convertedEntity = convert(entity);
            return this.validateAgainstRestrictions(fieldName, (T) convertedEntity, errors, repo);
        } catch (IllegalArgumentException iae) {
            super.addError(fieldName, entity, super.getSchemaType().getName(), ErrorType.INVALID_DATATYPE, errors);
            return false;
        }
    }
}
