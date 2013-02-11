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


package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.UnversionedResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.context.resolver.SecurityEventContextResolver;
import org.slc.sli.api.service.query.UriInfoToApiQueryConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.NeutralQuery.SortOrder;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides read access to SecurityEvents through the /securityEvent path.
 * For more information, see the schema for the $$securityEvent$$ entity.
 *
 * @author ldalgado
 */
@Component
@Scope("request")
@Path("securityEvent")
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class SecurityEventResource extends UnversionedResource {

    public static final String RESOURCE_NAME = "securityEvent";
    public static final List<String> WATCHED_APP = Arrays.asList("SimpleIDP");
    private final EntityDefinitionStore entityDefs;

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;
    
    
    @Autowired
    SecurityEventContextResolver resolver;
    private final UriInfoToApiQueryConverter queryConverter;

    @Autowired
    public SecurityEventResource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
        this.queryConverter = new UriInfoToApiQueryConverter();
    }

    public Response createSecurityEvent(EntityBody newSecurityEvent, @Context final UriInfo uriInfo) {
        return super.post(newSecurityEvent, uriInfo);
    }

    @GET
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW })
    @Override
    public Response getAll(@Context final UriInfo uriInfo) {
        return retrieveEntities(uriInfo);
    }

    private Response retrieveEntities(final UriInfo uriInfo) {
        EntityDefinition entityDef = entityDefs.lookupByResourceName(RESOURCE_NAME);
        NeutralQuery mainQuery = queryConverter.convert(uriInfo);
        mainQuery.setSortBy("timeStamp");
        mainQuery.setSortOrder(SortOrder.descending);
        mainQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, resolver.findAccessible(null)));

        List<EntityBody> results = new ArrayList<EntityBody>();
       /* for (Entity entity : repo.findAll("securityEvent", mainQuery)) {
            
            results.add(new EntityBody(entity.getBody()));
        }*/
        for (EntityBody body : entityDef.getService().list(mainQuery)) {
            results.add(body);
        }
        debug("Found [{}] SecurityEvents!", results.size());
        return Response.ok(new EntityResponse(entityDef.getType(), results)).build();
    }
}
