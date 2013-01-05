/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.strategy.AbstractBlacklistStrategy;

/**
 * Validates contents of an entity body
 *
 * @author tshewchuk
 *
 */
@Component
public class CustomEntityValidator {

    @Resource(name = "validationStrategyList")
    private List<AbstractBlacklistStrategy> validationStrategyList;

    private static final String CUSTOM_ENTITY = "custom_entities";

    /**
     * Validate a custom entity by checking its field names against the blacklists.
     *
     * @param entityId
     *            Custom Entity ID
     * @param entityType
     *            Entity type (must be "custom_entities")
     * @param entityBody
     *            Custom Entity to be validated
     *
     * @throws EntityValidationException
     *
     */
    public void validate(String entityId, String entitytype, EntityBody entityBody) throws EntityValidationException {
        List<ValidationError> errorList = new ArrayList<ValidationError>();
        validate(entityId, entitytype, entityBody, errorList);

        if (!errorList.isEmpty()) {
            throw new EntityValidationException(entityId, entitytype, errorList);
        }
    }

    /**
     * Validate a custom entity by checking its field names against the blacklists.
     *
     * @param entityId
     *            Custom Entity ID
     * @param entityType
     *            Entity type (must be "custom_entities")
     * @param entityBody
     *            Custom Entity to be validated
     * @param errorList
     *            List of errors encountered during validation
     *
     * @throws EntityValidationException
     *
     */
    @SuppressWarnings("unchecked")
    private void validate(String entityId, String entitytype, EntityBody entityBody, List<ValidationError> errorList)
            throws EntityValidationException {
        if ((entitytype == CUSTOM_ENTITY) && !entityBody.isEmpty()) {
            for (String fieldName : entityBody.keySet()) {
                // Remove valid characters before checking.
                String fieldNameToCheck = fieldName.replaceAll("<", "").replaceAll(">", "");
                for (AbstractBlacklistStrategy abstractBlacklistStrategy : validationStrategyList) {
                    if (!abstractBlacklistStrategy.isValid("", fieldNameToCheck)) {
                        errorList.add(0, new ValidationError(ValidationError.ErrorType.INVALID_FIELD_CONTENT, fieldName,
                                fieldName, null));
                    }
                }

                // If field contains sub-fields, check them, also.
                Object value = entityBody.get(fieldName);
                if (value instanceof Map<?, ?>) {
                    EntityBody subEntity = new EntityBody((Map<? extends String, ? extends Object>) value);
                    validate(entityId, entitytype, subEntity, errorList);
                }
            }
        }
    }
}
