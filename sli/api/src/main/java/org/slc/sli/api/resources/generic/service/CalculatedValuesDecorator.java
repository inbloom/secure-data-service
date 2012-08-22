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
package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.CalculatedData;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 * Adds calculated data to the entities
 *
 */
@Component
public class CalculatedValuesDecorator implements EntityDecorator {
    @Override
    public void decorate(EntityBody entity, EntityDefinition definition, MultivaluedMap<String, String> queryParams) {
        List<String> params = queryParams.get(ParameterConstants.INCLUDE_CALCULATED);
        final Boolean includeCalculatedValues = Boolean.valueOf((params != null) ? params.get(0) : "false");

        if (includeCalculatedValues) {
            String entityId = (String) entity.get("id");
            CalculatedData<String> calculatedValues = definition.getService().getCalculatedValues(entityId);
            if (calculatedValues != null) {
                entity.put(ResourceConstants.CALCULATED_VALUE_TYPE, calculatedValues.getCalculatedValues());
            }
        }
    }
}
