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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    private EdOrgHelper helper;


    private EntityService service;


    public static final String RESOURCE_NAME = "applicationAuthorization";

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
    }

    @GET
    @Path("{appId}")
    @RightsAllowed({Right.EDORG_APP_AUTHZ, Right.EDORG_DELEGATE })
    public Response getAuthorization(@PathParam("appId") String appId) {
        
        EntityBody appAuth = getAppAuth(appId);
        if (appAuth == null) {
            //See if this is an actual app
            Entity appEntity = repo.findOne("application", new NeutralQuery(new NeutralCriteria("_id", "=", appId)));
            if (appEntity == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else {
                HashMap<String, Object> entity = new HashMap<String, Object>();
                entity.put("appId", appId);
                entity.put("authorized", false);
                return Response.status(Status.OK).entity(entity).build();
            }
        } else {
            HashMap<String, Object> entity = new HashMap<String, Object>();
            entity.put("appId", appId);
            List edOrgs = (List) appAuth.get("edorgs");
            entity.put("authorized", edOrgs.contains(SecurityUtil.getEdOrgId()));
            return Response.status(Status.OK).entity(entity).build();
        }
        
    }
    
    private EntityBody getAppAuth(String appId) {
        Iterable<EntityBody> appAuths = service.list(new NeutralQuery(new NeutralCriteria("applicationId", "=", appId)));
        for (EntityBody auth : appAuths) {
            return auth;
        }
        return null;
    }

    @PUT
    @Path("{appId}")
    @RightsAllowed({Right.EDORG_APP_AUTHZ, Right.EDORG_DELEGATE })
    public Response updateAuthorization(@PathParam("appId") String appId, EntityBody auth, @Context final UriInfo uriInfo) {
        if (!auth.containsKey("authorized")) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        
        EntityBody existingAuth = getAppAuth(appId);
        if (existingAuth == null) {
            //See if this is an actual app
            Entity appEntity = repo.findOne("application", new NeutralQuery(new NeutralCriteria("_id", "=", appId)));
            if (appEntity == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else {
                if (((Boolean) auth.get("authorized")).booleanValue()) { //being set to true. if false, there's no work to be done
                    //We don't have an appauth entry for this app, so create one
                    EntityBody body = new EntityBody();
                    body.put("applicationId", appId);
                    ArrayList<String> edorgs = new ArrayList<String>();
                    edorgs.add(SecurityUtil.getEdOrgId());
                    edorgs.addAll(getParentEdorgs());
                    edorgs.addAll(getChildEdorgs());
                    body.put("edorgs", edorgs);
                    service.create(body);
                }
                return Response.status(Status.NO_CONTENT).build();
            }
        } else {
            List<String> edorgs = (List<String>) existingAuth.get("edorgs");
            Set<String> edorgsCopy = new HashSet<String>(edorgs);
            if (((Boolean) auth.get("authorized")).booleanValue()) {
                edorgsCopy.add(SecurityUtil.getEdOrgId());
                edorgsCopy.addAll(getParentEdorgs());
                edorgsCopy.addAll(getChildEdorgs());
            } else {
                edorgsCopy.remove(SecurityUtil.getEdOrgId());
                edorgsCopy.removeAll(getParentEdorgs());
                edorgsCopy.removeAll(getChildEdorgs());
            }
            existingAuth.put("edorgs", new ArrayList(edorgsCopy));
            service.update((String) existingAuth.get("id"), existingAuth);
            return Response.status(Status.NO_CONTENT).build();
        }
    }

    List<String> getParentEdorgs() {
        return helper.getParentEdOrgs(helper.byId(SecurityUtil.getEdOrgId()));
    }

    Set<String> getChildEdorgs() {
        return helper.getChildEdOrgs(Arrays.asList(SecurityUtil.getEdOrgId()));
    }
    
    @GET
    @RightsAllowed({Right.EDORG_APP_AUTHZ, Right.EDORG_DELEGATE })
    public Response getAuthorizations(@Context UriInfo info) {
        Iterable<Entity> appQuery = repo.findAll("application", new NeutralQuery());
        Map<String, Entity> allApps = new HashMap<String, Entity>();
        for (Entity ent : appQuery) {
            allApps.put(ent.getEntityId(), ent);
        }
        
        Iterable<EntityBody> ents = service.list(new NeutralQuery(new NeutralCriteria("edorgs", "=", SecurityUtil.getEdOrgId())));
        
        List<Map> results = new ArrayList<Map>();
        for (EntityBody body : ents) {
            HashMap<String, Object> entity = new HashMap<String, Object>();
            String appId = (String) body.get("applicationId");
            entity.put("appId", appId);
            entity.put("authorized", true);
            results.add(entity);
            allApps.remove(appId);
        }
        for (Map.Entry<String, Entity> entry : allApps.entrySet()) {
            List<String> approvedEdorgs = (List<String>) entry.getValue().getBody().get("authorized_ed_orgs");
            if (approvedEdorgs != null && approvedEdorgs.contains(SecurityUtil.getEdOrgId())) {
                HashMap<String, Object> entity = new HashMap<String, Object>();
                entity.put("appId", entry.getKey());
                entity.put("authorized", false);
                results.add(entity);
            }
        }
        return Response.status(Status.OK).entity(results).build();
    }

}
