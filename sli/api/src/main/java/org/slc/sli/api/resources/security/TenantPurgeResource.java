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

package org.slc.sli.api.resources.security;

import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.enums.Right;

/**
 * Resource available to SEA and Sandbox administrators to perform a tenant-level purge of the
 * database.
 *
 * @author tshewchuk
 */
@Component
@Scope("request")
@Path("/purge")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class TenantPurgeResource {

    /**
     * Purge a tenant from the database.
     *
     * @param reqBody
     *            Request to perform tenant-level purge
     * @param uriInfo
     *            URI information
     *
     * @return Response
     *         Result of the purge request
     */
    @POST
    public Response purge(Map<String, String> reqBody, @Context final UriInfo uriInfo) {
        String tenantId = reqBody.get(ResourceConstants.ENTITY_METADATA_TENANT_ID);

        // Ensure the user is the correct admin.
        if (!SecurityUtil.hasRight(Right.TENANT_PURGE)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to purge tenant " + tenantId);
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        Response resp = purgeTenant(tenantId);
        return resp;
    }

    /**
     * Perform a tenant-level purge of the database
     *
     * @param tenantId
     *            The tenant to purge
     *
     * @return Response
     *         Result of the purge request
     */
    public Response purgeTenant(final String tenantId) {
        return Response.status(Status.OK).build();
    }

}
