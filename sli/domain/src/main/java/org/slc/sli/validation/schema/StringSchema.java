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
import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.slc.sli.validation.strategy.AbstractBlacklistStrategy;

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

    private List<AbstractBlacklistStrategy> validationRuleList;
    private List<AbstractBlacklistStrategy> relaxedValidationRuleList;

    // Constructors
    public StringSchema() {
        this(NeutralSchemaType.STRING.getName(), new ArrayList<AbstractBlacklistStrategy>(), new ArrayList<AbstractBlacklistStrategy>());
    }

    public StringSchema(String xsdType) {
        this(xsdType, new ArrayList<AbstractBlacklistStrategy>(), new ArrayList<AbstractBlacklistStrategy>());
    }

    /**
     * Constructor with parameters for the lists of AbstractBlacklistStrategy instances which will be applied,
     * depending on AppInfo
     *
     * @param validationRuleList
     * @param relaxedValidationRuleList
     */
    public StringSchema(List<AbstractBlacklistStrategy> validationRuleList, List<AbstractBlacklistStrategy> relaxedValidationRuleList) {
        this(NeutralSchemaType.STRING.getName(), validationRuleList, relaxedValidationRuleList);
    }

    /**
     * Constructor with parameters the xsdType and the lists of AbstractBlacklistStrategy instances which will be
     * applied, depending on AppInfo
     *
     * @param xsdType
     * @param validationRuleList
     * @param relaxedValidationRuleList
     */
    public StringSchema(String xsdType, List<AbstractBlacklistStrategy> validationRuleList, List<AbstractBlacklistStrategy> relaxedValidationRuleList) {
        super(xsdType);
        this.validationRuleList = validationRuleList;
        this.relaxedValidationRuleList = relaxedValidationRuleList;
    }

    // Methods

    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.STRING;
    }




    @Override
    public Object convert(Object value) {
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
        if (isRelaxedBlacklisted()) {
            for (AbstractBlacklistStrategy validationRule : relaxedValidationRuleList) {
                boolean isValid = validationRule.isValid("StringSchemaContext", data);
                if (!addError(isValid, fieldName, entity, "Invalid value caught by relaxed blacklisting strategy: "
                        + validationRule.getIdentifier(), ErrorType.INVALID_VALUE, errors)) {
                    return false;
                }
            }
        } else {
            for (AbstractBlacklistStrategy validationRule : validationRuleList) {
                boolean isValid = validationRule.isValid("StringSchemaContext", data);
                if (!addError(isValid, fieldName, entity, "Invalid value caught by strict blacklisting strategy: "
                        + validationRule.getIdentifier(), ErrorType.INVALID_VALUE, errors)) {
                    return false;
                }
            }
        }

        return true;
    }

}
