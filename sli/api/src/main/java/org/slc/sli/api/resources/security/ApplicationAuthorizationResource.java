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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.mongodb.util.Hash;

/**
 *
 */
@Component
@Scope("request")
@Path("/applicationAuthorization")
@Produces({ HypermediaType.JSON + ";charset=utf-8" })
public class ApplicationAuthorizationResource {

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Autowired
    private DelegationUtil delegationUtil;

    private EntityService service;
    private EntityService applicationService;
    private EntityService edOrgService;

    public static final String RESOURCE_NAME = "applicationAuthorization";

    public static final String UUID = "uuid";
    public static final String APP_ID = "applicationId";
    public static final String EDORG_IDS = "edorgs";
    public static final String STATE_ORGANIZATION_ID = "stateOrganizationId";
    public static final String NAME_OF_INSTITUTION = "nameOfInstitution";
    public static final String CLIENT_ID = "clientId";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    public static final String RESOURCE_APPLICATION = "apps";
    public static final String RESOURCE_EDORG = "educationOrganization";

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(RESOURCE_NAME);
        service = def.getService();

        EntityDefinition appDef = store.lookupByResourceName(RESOURCE_APPLICATION);
        applicationService = appDef.getService();

        EntityDefinition edOrgDef = store.lookupByResourceName(ResourceNames.EDUCATION_ORGANIZATIONS);
        edOrgService = edOrgDef.getService();
    }

    @GET
    @Path("{" + UUID + "}")
    @RightsAllowed({Right.EDORG_APP_AUTHZ, Right.EDORG_DELEGATE })
    public Response getAuthorization(@PathParam(UUID) String uuid) {

        if (uuid != null) {
            EntityBody entityBody = service.get(uuid);
            if (entityBody != null) {
                return Response.status(Status.OK).entity(entityBody).build();
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    @RightsAllowed({Right.EDORG_APP_AUTHZ, Right.EDORG_DELEGATE })
    public Response createAuthorization(EntityBody newAppAuth, @Context final UriInfo uriInfo) {


        //make sure we don't already have an entry for this app
        Iterable<EntityBody> existingAuths = service.list(new NeutralQuery(new NeutralCriteria(APP_ID, "=", newAppAuth.get(APP_ID))));
        if (existingAuths.iterator().hasNext()) {
            EntityBody body = new EntityBody();
            body.put("message", "ApplicationAuthorization for the application already exists.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        //Make sure the app exists
        Entity appEnt = repo.findOne("application", new NeutralQuery(new NeutralCriteria("_id", "=", newAppAuth.get(APP_ID))));
        if (appEnt != null) {
            EntityBody body = new EntityBody();
            body.put("message", "Cannot create application authorization for application that does not exist.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        verifyAccess((Collection<String>) newAppAuth.get(EDORG_IDS));

        String uuid = service.create(newAppAuth);
        String uri = uriToString(uriInfo) + "/" + uuid;
        return Response.status(Status.CREATED).header("Location", uri).build();
    }

    @PUT
    @Path("{" + UUID + "}")
    @RightsAllowed({Right.EDORG_APP_AUTHZ, Right.EDORG_DELEGATE })
    public Response updateAuthorization(@PathParam(UUID) String uuid, EntityBody auth, @Context final UriInfo uriInfo) {

        EntityBody oldAuth = service.get(uuid);

        if (!oldAuth.get(APP_ID).equals(auth.get(APP_ID))) {
            EntityBody body = new EntityBody();
            body.put("message", "applicationId is read only");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        // Ensure uniqeness accross all edorgs
        List<String> edorgs = (List) auth.get(EDORG_IDS);
        Set<String> unique = new LinkedHashSet<String>(edorgs);
        edorgs = new ArrayList<String>(unique);
        auth.put(EDORG_IDS, edorgs);

        Set<String> oldEdorgs = new HashSet((List<String>) oldAuth.get(EDORG_IDS));
        
        //symmetric difference is both the items that are unique to either list, e.g. what's been added and removed
        Set<String> delta = Sets.symmetricDifference(unique, oldEdorgs);
        verifyAccess(delta);
        boolean status = service.update(uuid, auth);
        if (status) {
            return Response.status(Status.NO_CONTENT).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }


    @GET
    @RightsAllowed({Right.EDORG_APP_AUTHZ, Right.EDORG_DELEGATE })
    public Response getAuthorizations(@Context UriInfo info) {
        Iterable<EntityBody> ents = service.list(new NeutralQuery());
        List<EntityBody> results = new ArrayList<EntityBody>();
        for (EntityBody body : ents) {
            results.add(body);
        }
        return Response.status(Status.OK).entity(results).build();
    }

    private static String uriToString(UriInfo uri) {
        return uri.getBaseUri() + uri.getPath().replaceAll("/$", "");
    }

    private void verifyAccess(Collection<String> edOrgs) throws AccessDeniedException {
       // String edOrgId = SecurityUtil.getEdOrgId();

        //List<String> delegateEdOrgs = delegationUtil.getAppApprovalDelegateEdOrgs();
        //  if (!appAuthEdOrgId.equals(edOrgId) && !delegateEdOrgs.contains(appAuthEdOrgId)) {
        //     throw new AccessDeniedException("User can only access " + edOrgId);
        //  }
    }

}
