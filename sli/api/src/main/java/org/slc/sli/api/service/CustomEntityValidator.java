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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.strategy.AbstractBlacklistStrategy;

/**
 * Validates field names of a custom entity.
 *
 * @author tshewchuk
 *
 */
@Component
public class CustomEntityValidator {

    @Resource(name = "validationStrategyList")
    private List<AbstractBlacklistStrategy> validationStrategyList;

    /**
     * Validate a custom entity by checking its field names against the blacklists.
     *
     * @param entityId
     *            Custom Entity ID
     * @param entityType
     *            Entity type (must match PathConstants.CUSTOM_ENTITIES)
     * @param entityBody
     *            Custom Entity to be validated
     *
     */
    public List<ValidationError> validate(String entityId, String entitytype, EntityBody entityBody) {
        List<ValidationError> errorList = new ArrayList<ValidationError>();
        if ((entitytype == PathConstants.CUSTOM_ENTITIES) && !entityBody.isEmpty()) {
            validate(entityId, entitytype, entityBody, errorList);
        }
        return errorList;
    }

    /**
     * Validate a custom entity by checking its field names against the blacklists.
     *
     * @param entityId
     *            Custom Entity ID
     * @param entityType
     *            Entity type (must match PathConstants.CUSTOM_ENTITIES)
     * @param entityBody
     *            Custom Entity to be validated
     * @param errorList
     *            List of errors encountered during validation
     *
     */
    private void validate(String entityId, String entitytype, EntityBody entityBody, List<ValidationError> errorList) {
        for (String fieldName : entityBody.keySet()) {
            // Remove valid characters before checking.
            String fieldNameToCheck = fieldName.replaceAll("<", "").replaceAll(">", "");
            for (AbstractBlacklistStrategy abstractBlacklistStrategy : validationStrategyList) {
                if (!abstractBlacklistStrategy.isValid("", fieldNameToCheck)) {
                    errorList.add(new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, fieldName,
                            fieldName, null));
                }
            }

            // If field contains sub-fields, check them, also.
            EntityBody subEntityBody = createEntityBody(entityBody.get(fieldName));
            validate(entityId,  entitytype, subEntityBody, errorList);
        }
    }

    /**
     * Create a custom entity containing all field names contained within some Object.
     *
     * @param object
     *            Object containing possible field names.
     *
     * @return EntityBody
     *         Custom entity containing all field names contained within object.
     *
     */
    @SuppressWarnings("unchecked")
    private EntityBody createEntityBody(Object object) {
        EntityBody entityBody = new EntityBody();
        if (object instanceof Map<?, ?>) {  // Map.
            entityBody = new EntityBody((Map<? extends String, ? extends Object>) object);
        } else if (object instanceof Object[]) {  // Array of Something.
            for (Object elem : (Object[]) object) {
                entityBody = createEntityBody(elem);
            }
        } else if (object instanceof Collection<?>) {  // Collection of Something.
            for (Object elem : (Collection<Object>) object) {
                entityBody = createEntityBody(elem);
            }
        }  // Else, string or special type.  Search no more!

        return entityBody;
    }
}
