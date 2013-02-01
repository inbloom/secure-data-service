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
package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 * Adds custom data to the entities
 *
 */
@Component
public class CustomEntityDecorator implements EntityDecorator {

    @Override
    public void decorate(EntityBody entity, EntityDefinition definition, MultivaluedMap<String, String> queryParams) {
        List<String> params = queryParams.get(ParameterConstants.INCLUDE_CUSTOM);
        final Boolean includeCustomEntity = Boolean.valueOf((params != null) ? params.get(0) : "false");

        if (includeCustomEntity) {
            String entityId = (String) entity.get("id");
            EntityBody custom = definition.getService().getCustom(entityId);
            if (custom != null) {
                entity.put(ResourceConstants.CUSTOM, custom);
            }
        }
    }
}
