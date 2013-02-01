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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.slc.sli.validation.schema.Annotation.AnnotationType;

/**
 *
 * SLI List Schema which validates a list or collection of entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class ListSchema extends NeutralSchema {

    // Attributes
    private List<NeutralSchema> list = new LinkedList<NeutralSchema>();

    // Constructors
    public ListSchema() {
        this(NeutralSchemaType.LIST.getName());
    }

    public ListSchema(String xsdType) {
        super(xsdType);
    }

    // Methods

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.LIST;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    public void setList(List<NeutralSchema> list) {
        this.list = list;
    }

    public List<NeutralSchema> getList() {
        return list;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    /**
     * This is a temp hack to fix a bug.
     * Annotations do not inherit from list to it's members
     * The solution to this problem is to remove the whole concept of multiple schema types for a
     * list, which is now handled by ChoiceSchema
     */
    public void updateAnnotations() {
        AppInfo info = (AppInfo) getAnnotation(AnnotationType.APPINFO);
        if (info != null) {
            for (NeutralSchema itemSchema : getList()) {
                itemSchema.inheritAnnotations(info);
            }
        }
    }

    @Override
    public Object convert(Object value) {
        if (String.class.isInstance(value)) {
            return Arrays.asList(((String) value).split(","));
        } else if (Map.class.isInstance(value)) {
            Map<String, Object> values = (Map<String, Object>) value;
            List<Object> list = new ArrayList<Object>();

            for (Map.Entry<String, Object> e : values.entrySet()) {
                if (e.getValue() == null) {
                    list.add(StringUtils.EMPTY);
                } else {
                    list.add(e.getValue());
                }
            }

            return list;
        }

        return value;
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
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        boolean isValid = true;

        Object convertedEntity = convert(entity);

        if (convertedEntity instanceof List) {
            List<?> entityList = (List<?>) convertedEntity;
            for (Object fieldEntity : entityList) {

                // Allow validation according to ANY item Schemas in the ListSchema list (xs:choice
                // scenario)
                boolean isFieldValid = false;
                for (NeutralSchema itemSchema : getList()) {

                    // Choice scenario will not provide validation errors (null)
                    if (itemSchema.validate(fieldName, fieldEntity, errors, repo)) {
                        isFieldValid = true;
                        break;
                    }
                }

                if (!isFieldValid) {
                    isValid = false;
                    if (errors == null) {
                        return false;
                    }
                }
            }

            if (getProperties() != null) {
                for (Entry<String, Object> entry : getProperties().entrySet()) {
                    if (Restriction.isRestriction(entry.getKey())) {
                        long restrictionValue = Long.parseLong(entry.getValue().toString());
                        switch (Restriction.fromValue(entry.getKey())) {
                            case LENGTH:
                                if (!addError(entityList.size() == restrictionValue, fieldName, convertedEntity, "length="
                                        + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                                    return false;
                                }
                                break;
                            case MIN_LENGTH:
                                if (!addError(entityList.size() >= restrictionValue, fieldName, convertedEntity, "min-length="
                                        + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                                    return false;
                                }
                                break;
                            case MAX_LENGTH:
                                if (!addError(entityList.size() <= restrictionValue, fieldName, convertedEntity, "max-length="
                                        + restrictionValue, ErrorType.INVALID_VALUE, errors)) {
                                    return false;
                                }
                                break;
                        }
                    }
                }
            }

        } else {
            return addError(false, fieldName, convertedEntity, "List", ErrorType.INVALID_DATATYPE, errors);
        }

        return isValid;
    }

    @Override
    protected Annotation getAnnotation(Annotation.AnnotationType type) {
        Annotation annotation = super.getAnnotation(type);
        if (annotation != null) {
            return annotation;
        }
        return list.get(0).getAnnotation(type);
    }

}
