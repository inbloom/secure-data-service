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
package org.slc.sli.api.search.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.search.service.SearchResourceService;

/**
 * Resource for handling search requests
 *
 */
@Component
@Scope("request")
public class SearchResource extends GenericResource {

    @Autowired
    SearchResourceService resourceService;

    @GET
    public Response get(@Context final UriInfo uriInfo) {

        return getAll(ResourceTemplate.ONE_PART, null, uriInfo, false);
    }

    @GET
    @Path("{entity}")
    public Response get(@PathParam("entity") final String entity, @Context final UriInfo uriInfo) {
        return getAll(ResourceTemplate.SEARCH, entity, uriInfo, false);

    }

    @GET
    @Path("{entity}")
    @Consumes("application/vnd.slc.search.full+json")
    public Response getAndRoute(@PathParam("entity") final String entity, @Context final UriInfo uriInfo) {

        return getAll(ResourceTemplate.SEARCH, entity, uriInfo, true);
    }

    public Response getAll(ResourceTemplate resourceTemplate, final String entity, final UriInfo uriInfo, final boolean routeToEntityApp) {

        return getAllResponseBuilder.build(uriInfo, resourceTemplate, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {

                return resourceService.list(resource, entity, uriInfo.getRequestUri(), routeToEntityApp);
            }
        });
    }

}
