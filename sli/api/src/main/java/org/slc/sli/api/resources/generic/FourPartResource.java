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
package org.slc.sli.api.resources.generic;

import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author jstokes
 * @author pghosh
 * @author srupasinghe
 *
 */
@Component
@Scope("request")
public class FourPartResource extends GenericResource {

    @GET
    public Response get(@Context final UriInfo uriInfo,
                        @PathParam("id") final String id) {

        return getAllResponseBuilder.build(uriInfo, ResourceTemplate.FOUR_PART, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {
                final Resource base = resourceHelper.getBaseName(uriInfo, ResourceTemplate.FOUR_PART);
                final Resource association = resourceHelper.getAssociationName(uriInfo, ResourceTemplate.FOUR_PART);

                return resourceService.getEntities(base, id, association, resource, uriInfo.getRequestUri());
            }
        });

    }
}
