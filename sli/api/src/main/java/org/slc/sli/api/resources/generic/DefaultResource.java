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

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.CustomEntityResource;
import org.slc.sli.api.resources.v1.aggregation.CalculatedDataListingResource;
import org.slc.sli.api.util.PATCH;
import org.slc.sli.domain.CalculatedData;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


/**
 * Resource for handling one part URIs
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 */

@Component
@Scope("request")
public class DefaultResource extends GenericResource implements CustomEntityReturnable, CalculatedDataListable {

    @GET
    public Response getAll(@Context final UriInfo uriInfo) {

        return getAllResponseBuilder.build(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {

                return resourceService.getEntities(resource, uriInfo.getRequestUri(), false);
            }
        });

    }

    @POST
    public Response post(final EntityBody entityBody,
                         @Context final UriInfo uriInfo) {

        return defaultResponseBuilder.build(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.POST, new ResourceLogic() {
            @Override
            public Response run(Resource resource) {
                final String id = resourceService.postEntity(resource, entityBody);

                final String uri = ResourceUtil.getURI(uriInfo, PathConstants.V1,
                        resource.getResourceType(), id).toString();

                return Response.status(Response.Status.CREATED).header("Location", uri).build();
            }
        });

    }

    @GET
    @Path("{id}")
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
    @Path("{id}")
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
    @Path("{id}")
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
    @Path("{id}")
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

    public CustomEntityResource getCustomResource(final String id, final UriInfo uriInfo) {
        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.CUSTOM);
        return new CustomEntityResource(id,
                resourceHelper.getEntityDefinition(resource.getResourceType()));
    }

    public CalculatedDataListingResource<String> getCalculatedValueResource(final String id, final UriInfo uriInfo) {
        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.CALCULATED_VALUES);
        CalculatedData<String> data = resourceService.getCalculatedData(resource, id);

        return new CalculatedDataListingResource<String>(data);
    }

}
