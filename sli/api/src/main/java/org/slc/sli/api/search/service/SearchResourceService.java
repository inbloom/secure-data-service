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
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.service.query.ApiQuery;

/**
 * Search service
 *
 */

@Component
public class SearchResourceService {

    @Autowired
    private ResourceHelper resourceHelper;

    public ServiceResponse list(Resource resource, URI queryUri) {

        // Call BasicService to query the elastic search repo
        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        ApiQuery apiQuery = new ApiQuery(queryUri);
        Iterable<EntityBody> entityBodies = definition.getService().list(apiQuery);

        // return results
        List<EntityBody> retList = (List<EntityBody>) entityBodies;
        return new ServiceResponse(retList, retList.size());
    }
}
