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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.constants.Constraints;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.context.resolver.SecurityEventContextResolver;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.query.UriInfoToApiQueryConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.NeutralQuery.SortOrder;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides read access to SecurityEvents through the /securityEvent path.
 * For more information, see the schema for the $$securityEvent$$ entity.
 *
 * This is an unversioned resource, however it does not extend
 * {@link org.slc.sli.api.resources.generic.UnversionedResource}, because
 * that extends {@link org.slc.sli.api.resources.generic.DefaultResource} and has
 * implementations for several completely inappropriate HTTP methods for security events,
 * such as DELETE, POST, PUT, and PATCH.
 *
 * @author ldalgado
 * @author Andrew D. Ball
 */
@Component
@Path("securityEvent")
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class SecurityEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityEventResource.class);

    public static final String RESOURCE_NAME = "securityEvent";

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    @Autowired
    SecurityEventContextResolver resolver;

    private final UriInfoToApiQueryConverter queryConverter;

    public SecurityEventResource() {
        this.queryConverter = new UriInfoToApiQueryConverter();
    }

    @GET
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW })
    public Response getAll(@Context final UriInfo uriInfo) {
        List<EntityBody> results = retrieveEntities(uriInfo, null);

        return Response.ok(new EntityResponse(RESOURCE_NAME, results)).build();
    }

    @GET
    @Path("{id}")
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW})
    public Response getWithId(@PathParam("id") final String idList, @Context final UriInfo uriInfo) {
        // TODO find a more appropriate place to put id list parsing code

        Set<String> requestedIds = new HashSet<String>();
        for (String id: idList.split(",")) {
            requestedIds.add(id);
        }

        if (requestedIds.size() > Constraints.MAX_MULTIPLE_UUIDS) {
            String errorMessage = "Too many GUIDs: " + requestedIds.size() + " (input) vs "
                    + Constraints.MAX_MULTIPLE_UUIDS + " (allowed)";
            LOG.error(errorMessage);
            throw new PreConditionFailedException(errorMessage);
        }


        List<EntityBody> results = retrieveEntities(uriInfo, requestedIds);

        if (requestedIds.size() == 1 && results.isEmpty()) {
            throw new EntityNotFoundException(requestedIds.iterator().next());
        }

        if (results.size() == 1) {
            return Response.ok(new EntityResponse(RESOURCE_NAME, results.get(0))).build();
        } else {
            return Response.ok(new EntityResponse(RESOURCE_NAME, results)).build();
        }
    }

    /**
     * The one and only method to find security events in the database on behalf of API clients.
     *
     * Uses SecurityEventContextResolver to determine what security events a user has access to.
     *
     * @param uriInfo
     * @param requestedIds (optional) set of ids requested explicitly
     * @return
     */
    private List<EntityBody> retrieveEntities(final UriInfo uriInfo, Set<String> requestedIds) {

        NeutralQuery mainQuery = queryConverter.convert(uriInfo);
        mainQuery.setSortBy("timeStamp");
        mainQuery.setSortOrder(SortOrder.descending);

        Set<String> idsToFilter = new HashSet<String>();
        idsToFilter.addAll(resolver.findAccessible(null));
        if (requestedIds != null) {
            idsToFilter.retainAll(requestedIds);
        }

        mainQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, idsToFilter));

        Iterable<Entity> entities = repo.findAll(RESOURCE_NAME, mainQuery);

        List<EntityBody> results = new ArrayList<EntityBody>();
        for (Entity entity : entities) {
            results.add(convertEntityToResponseForm(entity));
        }
        LOG.debug("Found [{}] SecurityEvents!", results.size());

        return results;
    }


    /**
     * Transforms what we get from the database into a form suitable to present in API results.
     */
    private EntityBody convertEntityToResponseForm(Entity entity) {

        EntityBody result = new EntityBody();
        for (String key : entity.getBody().keySet()) {
            result.put(key, entity.getBody().get(key));
        }
        result.put("id", entity.getEntityId());
        result.put("entityType", entity.getType());

        return result;

    }

}
