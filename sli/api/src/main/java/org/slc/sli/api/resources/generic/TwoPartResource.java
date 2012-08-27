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

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.util.PATCH;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Resource for handling two part URIs
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 *
 */
@Component
@Scope("request")
public class TwoPartResource extends GenericResource {


    @GET
    public Response getWithId(@PathParam("id") final String id,
                              @Context final UriInfo uriInfo) {

        return getResponseBuilder.build(uriInfo, ResourceTemplate.TWO_PART, ResourceMethod.GET, new GenericResource.GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {
                return resourceService.getEntitiesByIds(resource, id, uriInfo.getRequestUri());
            }
        });

    }

    @PUT
    public Response put(@PathParam("id") final String id,
                        final EntityBody entityBody,
                        @Context final UriInfo uriInfo) {

        return defaultResponseBuilder.build(uriInfo, ResourceTemplate.TWO_PART, ResourceMethod.PUT, new ResourceLogic() {

            public Response run(Resource resource) {
                resourceService.putEntity(resource, id, entityBody);

                return Response.status(Response.Status.NO_CONTENT).build();
            }
        });
    }

    @DELETE
    public Response delete(@PathParam("id") final String id,
                           @Context final UriInfo uriInfo) {

        return defaultResponseBuilder.build(uriInfo, ResourceTemplate.TWO_PART, ResourceMethod.DELETE, new ResourceLogic() {
            @Override
            public Response run(Resource resource) {
                resourceService.deleteEntity(resource, id);

                return Response.status(Response.Status.NO_CONTENT).build();
            }
        });

    }

    @PATCH
    public Response patch(@PathParam("id") final String id,
                          final EntityBody entityBody,
                          @Context final UriInfo uriInfo) {

        return defaultResponseBuilder.build(uriInfo, ResourceTemplate.TWO_PART, ResourceMethod.PATCH, new ResourceLogic() {

            @Override
            public Response run(Resource resource) {
                resourceService.patchEntity(resource, id, entityBody);

                return Response.status(Response.Status.NO_CONTENT).build();
            }
        });

    }

}
