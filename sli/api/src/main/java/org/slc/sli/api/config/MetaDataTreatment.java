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


package org.slc.sli.api.config;

import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.security.CustomRoleResource;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.domain.Entity;

/**
 * Add the "updated" and "created" fields to the response body.
 *
 * @author jnanney
 *
 */
@Component
public class MetaDataTreatment implements Treatment {

    public static final String METADATA = "metaData";


    @Override
    public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {
        exposed.remove(METADATA);
        return exposed;
    }

    @Override
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, Entity entity) {
        if (stored.get("entityType").equals("roles")
                || stored.get("entityType").equals("applicationAuthorization")
                || stored.get("entityType").equals("application")
                || stored.get("entityType").equals("onboarding")
                || stored.get("entityType").equals(CustomRoleResource.RESOURCE_NAME)) {
            stored.put(METADATA, entity.getMetaData());
        }
        //stored.put(METADATA, entity.getMetaData());

        return stored;
    }

}
