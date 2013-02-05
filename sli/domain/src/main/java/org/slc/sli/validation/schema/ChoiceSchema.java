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
import java.util.Map;
import java.util.Map.Entry;

import org.slc.sli.domain.Repository;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * Schema which validates choices.
 *
 * A choice can have minOccurs and maxOccurs attributes. These indicate how many of the
 * elements are allowed. Default is 1 -- only one element is allowed.
 *
 * A choice element can have minOccurs and maxOccurs attributes. This indicates how many times
 * the same element is allowed. maxOccurs can also be set to UNBOUNDED.
 *
 * @author asaarela
 * @author nbrown
 *
 */
public class ChoiceSchema extends NeutralSchema {

    private static final String MAX_OCCURS = "maxOccurs";
    private static final String MIN_OCCURS = "minOccurs";

    // Match the Apache XMLSchema type for unbounded values.
    public static final long UNBOUNDED = Long.MAX_VALUE;

    public ChoiceSchema(long minOccurs, long maxOccurs) {
        super(NeutralSchemaType.CHOICE.getName());

        getProperties().put(MIN_OCCURS, minOccurs);
        getProperties().put(MAX_OCCURS, maxOccurs);
    }

    public ChoiceSchema(String name) {
        super(name);
    }

    public void setMinOccurs(long i) {
        getProperties().put(MIN_OCCURS, i);
    }

    public void setMaxOccurs(long i) {
        getProperties().put(MAX_OCCURS, i);
    }

    public long getMinOccurs() {
        return (Long) getProperties().get(MIN_OCCURS);
    }

    public long getMaxOccurs() {
        return (Long) getProperties().get(MAX_OCCURS);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {

        if (getMaxOccurs() == 1) {
            if (getMinOccurs() == 0 && entity == null) {
                return true;
            }
            if (!(entity instanceof Map)) {
                return addError(false, fieldName, entity, "Map", ErrorType.INVALID_DATATYPE, errors);
            }
            Map<?, ?> datum = (Map<?, ?>) entity;
            if (datum.size() != 1) {
                return addError(false, fieldName, entity, "Single Entry", ErrorType.INVALID_DATATYPE, errors);
            }
            Entry<?, ?> datumValue = datum.entrySet().iterator().next();
            if (!(datumValue.getKey() instanceof String)) {
                return addError(false, fieldName, entity, this.getFields().keySet().toArray(new String[0]),
                        ErrorType.INVALID_CHOICE_TYPE, errors);
            }
            String key = (String) datumValue.getKey();
            Object value = datumValue.getValue();

            NeutralSchema fieldSchema = this.getFields().get(key);
            if (fieldSchema == null) {
                return addError(false, fieldName, entity, this.getFields().keySet().toArray(new String[0]),
                        ErrorType.INVALID_CHOICE_TYPE, errors);
            }
            if (fieldSchema.validate(key, value, errors, repo)) {
                return true;
            }
        } else if (getMaxOccurs() > 1) {
            if (!(entity instanceof List)) {
                return addError(false, fieldName, entity, "List", ErrorType.INVALID_DATATYPE, errors);
            }
            if (((List) entity).size() < getMinOccurs()) {
                return addError(false, fieldName, entity, "min-length=" + this.getMinOccurs(), ErrorType.INVALID_VALUE,
                        errors);
            } else if (((List) entity).size() > getMaxOccurs()) {
                return addError(false, fieldName, entity, "max-length=" + this.getMinOccurs(), ErrorType.INVALID_VALUE,
                        errors);
            }

            List<Object> data = (List<Object>) entity;
            for (Object uncastedDatum : data) {
                if (!(uncastedDatum instanceof Map)) {
                    return addError(false, fieldName, entity, "Map", ErrorType.INVALID_DATATYPE, errors);
                }
                Map<?, ?> datum = (Map<?, ?>) uncastedDatum;
                if (datum.size() != 1) {
                    return addError(false, fieldName, entity, "Single Entry", ErrorType.INVALID_DATATYPE, errors);
                }
                Entry<?, ?> datumValue = datum.entrySet().iterator().next();
                if (!(datumValue.getKey() instanceof String)) {
                    return addError(false, fieldName, entity, this.getFields().keySet().toArray(new String[0]),
                            ErrorType.INVALID_CHOICE_TYPE, errors);
                }
                String key = (String) datumValue.getKey();
                Object value = datumValue.getValue();

                NeutralSchema fieldSchema = this.getFields().get(key);
                if (fieldSchema == null) {
                    return addError(false, fieldName, entity, this.getFields().keySet().toArray(new String[0]),
                            ErrorType.INVALID_CHOICE_TYPE, errors);
                }
                if (!fieldSchema.validate(key, value, errors, repo)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.CHOICE;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

}
