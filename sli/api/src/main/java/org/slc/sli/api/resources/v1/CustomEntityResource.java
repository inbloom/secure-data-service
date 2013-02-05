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


package org.slc.sli.api.resources.v1;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;


import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.util.ResourceUtil;

/**
 * Subresource for custom entities
 *
 */
public class CustomEntityResource {

    private final EntityDefinition entityDefinition;
    private final String entityId;
    private ResourceHelper resourceHelper;

    public CustomEntityResource(final String entityId, final EntityDefinition entityDefinition,
                                final ResourceHelper resourceHelper) {
        this.entityId = entityId;
        this.entityDefinition = entityDefinition;
        this.resourceHelper = resourceHelper;
    }

    /**
     * Read the contents of the custom resource for the given entity.
     *
     * @return the response to the GET request
     */
    @GET
    @Path("/")
    public Response read() {

        if (entityDefinition == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        EntityBody entityBody = entityDefinition.getService().getCustom(entityId);
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
    @Path("/")
    public Response createOrUpdatePut(final EntityBody customEntity) {

        if (entityDefinition == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDefinition.getService().createOrUpdateCustom(entityId, customEntity);
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
    @Path("/")
    public Response createOrUpdatePost(@Context final UriInfo uriInfo,
                                       final EntityBody customEntity) {

        if (entityDefinition == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDefinition.getService().createOrUpdateCustom(entityId, customEntity);
        String uri = ResourceUtil.getURI(uriInfo, resourceHelper.extractVersion(uriInfo.getPathSegments()),
                PathConstants.TEMP_MAP.get(entityDefinition.getResourceName()), entityId, PathConstants.CUSTOM_ENTITIES)
                .toString();
        return Response.status(Status.CREATED).header("Location", uri).build();
    }

    /**
     * Remove the custom resource for the given entity.
     *
     * @return the response to the DELETE request
     */
    @DELETE
    @Path("/")
    public Response delete() {

        if (entityDefinition == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDefinition.getService().deleteCustom(entityId);
        return Response.status(Status.NO_CONTENT).build();
    }
}
