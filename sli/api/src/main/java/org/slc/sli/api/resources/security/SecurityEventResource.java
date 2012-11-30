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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.NeutralQuery.SortOrder;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.domain.Repository;

/**
 *
 * Provides read access to SecurityEvents through the /securityEvent path.
 * For more information, see the schema for the $$securityEvent$$ entity.
 *
 * @author ldalgado
 */
@Component
@Scope("request")
@Path("securityEvent")
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class SecurityEventResource extends DefaultCrudEndpoint {

    public static final String          RESOURCE_NAME     = "securityEvent";
    public static final List<String>    WATCHED_APP       = Arrays.asList("SimpleIDP");
    private final EntityDefinitionStore entityDefs;

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    @Autowired
    public SecurityEventResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        this.entityDefs = entityDefs;
    }

    public Response createSecurityEvent(EntityBody newSecurityEvent, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.create(newSecurityEvent, headers, uriInfo);
    }

    @GET
    @RightsAllowed({Right.SECURITY_EVENT_VIEW})
    public Response getSecurityEvents(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        return retrieveEntities(offset, limit, uriInfo);
    }


    private Response retrieveEntities(final int offset, final int limit, final UriInfo uriInfo) {

        EntityDefinition entityDef = entityDefs.lookupByResourceName(RESOURCE_NAME);
        NeutralQuery mainQuery = new NeutralQuery();
        mainQuery.addCriteria(new NeutralCriteria("appId", NeutralCriteria.CRITERIA_IN, WATCHED_APP));
        mainQuery.setOffset(offset);
        mainQuery.setLimit(limit);
        mainQuery.setSortBy("timeStamp");
        mainQuery.setSortOrder(SortOrder.descending);

        List<EntityBody> results = new ArrayList<EntityBody>();
        for (EntityBody entityBody : entityDef.getService().list(mainQuery)) {
            results.add(entityBody);
        }
        debug("Found [" + results.size() + "] SecurityEvents!");
        return Response.ok(new EntityResponse(entityDef.getType(), results)).build();

    }
}
