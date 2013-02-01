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

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.domain.CalculatedData;

import java.net.URI;
import java.util.Map;

/**
 * Resource service.
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 *
 */

public interface ResourceService {
    public ServiceResponse getEntitiesByIds(Resource resource, String idList, URI requestURI);

    public ServiceResponse getEntities(Resource resource, URI requestURI, boolean getAllEntities);

    public ServiceResponse getEntities(Resource base, String id, Resource resource, URI requestURI);

    public ServiceResponse getEntities(Resource base, String id, Resource association,
                                        Resource resource, URI requestURI);

    public String postEntity(Resource resource, EntityBody entity);

    public void putEntity(Resource resource, String id, EntityBody entity);

    public void patchEntity(Resource resource, String id, EntityBody entity);

    public void deleteEntity(Resource resource, String id);

    public String getEntityType(Resource resource);

    public CalculatedData<String> getCalculatedData(Resource resource, String id);

    public CalculatedData<Map<String, Integer>> getAggregateData(Resource resource, String id);
}
