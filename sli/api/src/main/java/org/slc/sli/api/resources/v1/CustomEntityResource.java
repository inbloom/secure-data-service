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


package org.slc.sli.api.resources.v1;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.util.ResourceUtil;

/**
 * Subresource for custom entities
 *
 */
@Component
@Scope("request")
public class CustomEntityResource {

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    @Autowired
    private ResourceHelper resourceHelper;

    /**
     * Read the contents of the custom resource for the given entity.
     *
     * @return the response to the GET request
     */
    @GET
    public Response read(@Context final UriInfo uriInfo,
                         @PathParam("id") final String entityId) {

        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.CUSTOM);
        final EntityDefinition entityDef = entityDefinitionStore.lookupByResourceName(resource.getResourceType());

        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        EntityBody entityBody = entityDef.getService().getCustom(entityId);
        if (entityBody == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.OK).entity(entityBody).build();
    }

    /**
     * Set the contents of the custom resource for the given entity.
     *
     * @param customEntity
     *            the new entity to set
     * @return the response to the PUT request
     */
    @PUT
    public Response createOrUpdatePut(@Context final UriInfo uriInfo,
                                      @PathParam("id") final String entityId,
                                      final EntityBody customEntity) {

        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.CUSTOM);
        final EntityDefinition entityDef = entityDefinitionStore.lookupByResourceName(resource.getResourceType());

        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDef.getService().createOrUpdateCustom(entityId, customEntity);
        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Set the contents of the custom resource for the given entity.
     *
     * @param customEntity
     *            the new entity to set
     * @return the response to the POST request
     */
    @POST
    public Response createOrUpdatePost(final EntityBody customEntity,
                                       @Context final UriInfo uriInfo,
                                       @PathParam("id") final String entityId) {

        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.CUSTOM);
        final EntityDefinition entityDef = entityDefinitionStore.lookupByResourceName(resource.getResourceType());

        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDef.getService().createOrUpdateCustom(entityId, customEntity);
        String uri = ResourceUtil.getURI(uriInfo, PathConstants.V1,
                PathConstants.TEMP_MAP.get(entityDef.getResourceName()), entityId, PathConstants.CUSTOM_ENTITIES)
                .toString();
        return Response.status(Status.CREATED).header("Location", uri).build();
    }

    /**
     * Remove the custom resource for the given entity.
     *
     * @return the response to the DELETE request
     */
    @DELETE
    public Response delete(@Context final UriInfo uriInfo,
                           @PathParam("id") final String entityId) {

        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.CUSTOM);
        final EntityDefinition entityDef = entityDefinitionStore.lookupByResourceName(resource.getResourceType());

        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDef.getService().deleteCustom(entityId);
        return Response.status(Status.NO_CONTENT).build();
    }
}
