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
package org.slc.sli.api.search.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.Entity;

/**
 * Search service
 *
 */

@Component
public class SearchResourceService {

    @Autowired
    private ResourceHelper resourceHelper;

    public ServiceResponse list(Resource resource, URI queryUri) {

        List<EntityBody> entityBodies = null;

        // Temporary until teacher security is in place
        // If teacher, return unauthorized error
        if (isTeacher()) {
            throw new AccessDeniedException("Search currently available only for staff.");
        }

        // Call BasicService to query the elastic search repo
        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        ApiQuery apiQuery = new ApiQuery(queryUri);
        entityBodies = (List<EntityBody>) definition.getService().list(apiQuery);

        // return results
        return new ServiceResponse(entityBodies, entityBodies.size());
    }


    private boolean isTeacher() {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Entity entity = principal.getEntity();
        String type = entity != null ? entity.getType() : null;
        if (type != null && type.equals(EntityNames.TEACHER)) {
            return true;
        } else {
            return false;
        }
    }

}
