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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.cache.SessionCache;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * CRUD resource for custom roles.
 *
 * @author jnanney
 */
@Component
@Scope("request")
@Path("/customRoles")
@Produces({ HypermediaType.JSON + ";charset=utf-8" })
public class CustomRoleResource {

    @Autowired
    private EntityDefinitionStore store;


    @Value("${sli.sandbox.enabled}")
    protected boolean isSandboxEnabled;


    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Autowired
    private RoleInitializer roleInitializer;

    private EntityService service;

    @Autowired
    private RealmHelper realmHelper;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Resource
    private SessionCache sessions;
    
    public static final String RESOURCE_NAME = "customRole";

    protected static final String ERROR_DUPLICATE_ROLE = "Cannot list duplicate roles";
    protected static final String ERROR_INVALID_REALM = "Cannot modify custom roles for that realm/tenant";
    protected static final String ERROR_INVALID_RIGHT = "Invalid right listed in custom role document";
    protected static final String ERROR_MULTIPLE_DOCS = "Cannot create multiple custom role documents per realm/tenant";
    protected static final String ERROR_FORBIDDEN = "User does not have access to requested role document";
    protected static final String ERROR_DUPLICATE_RIGHTS = "Cannot have the same right listed more than once in a role";
    protected static final String ERROR_CHANGING_REALM_ID = "Cannot change the realmId on a custom role document";
    protected static final String ERROR_INVALID_REALM_ID = "Invalid realmId specified.";

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("customRole");
        service = def.getService();
    }

    @GET
    @RightsAllowed({Right.CRUD_ROLE })
    public Response readAll(@Context final UriInfo uriInfo,
            @DefaultValue("") @QueryParam("realmId") String realmId) {

        if (uriInfo.getQueryParameters() != null) {
            String defaultsOnly = uriInfo.getQueryParameters().getFirst("defaultsOnly");

            if (defaultsOnly != null && Boolean.valueOf(defaultsOnly).booleanValue()) {
                return Response.ok(roleInitializer.getDefaultRoles()).build();
            }
        }

        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        //If the user's edorg is mapped to more than one realm, then we have to use a
        //realmId param to figure out which to use.  Otherwise we can just return custom
        //roles for all the user's realms.
        Set<String> myRealms = realmHelper.getAssociatedRealmIds();
        Set<String> realmsToQuery = null;
        if (!realmId.isEmpty() && !myRealms.contains(realmId)) {
            return buildBadRequest(ERROR_INVALID_REALM_ID);
        } else {
            if (realmId.isEmpty()) {
                realmsToQuery = myRealms;
            } else {
                realmsToQuery = new HashSet<String>();
                realmsToQuery.add(realmId);
            }
        }

        NeutralQuery customRoleQuery = new NeutralQuery();
        customRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.CRITERIA_IN, realmsToQuery));

        Iterable<String> customRoles = repo.findAllIds("customRole", customRoleQuery);
        for (String id : customRoles) {
            EntityBody result = service.get(id);
            results.add(result);
        }
        return Response.ok(results).build();
    }

    @GET
    @Path("{id}")
    @RightsAllowed({Right.CRUD_ROLE })
    public Response read(@PathParam("id") String id, @Context final UriInfo uriInfo) {

        EntityBody customRole = service.get(id);
        if (!realmHelper.getAssociatedRealmIds().contains(customRole.get("realmId"))) {
            auditSecEvent(uriInfo, "Failed to read custom role with id: " + id + " wrong tenant + realm combination.");
            return Response.status(Status.FORBIDDEN).entity(ERROR_FORBIDDEN).build();
        }
        return Response.ok(customRole).build();
    }

    @POST
    @RightsAllowed({Right.CRUD_ROLE })
    public Response createCustomRole(EntityBody newCustomRole, @Context final UriInfo uriInfo) {

        Response res = validateRights(newCustomRole);
        if (res != null) {
            auditSecEvent(uriInfo, "Failed to create custom role rights validation failed.");
            return res;
        }
        res = validateUniqueRoles(newCustomRole);
        if (res != null) {
            auditSecEvent(uriInfo, "Failed to create custom role unique roles check failed.");
            return res;
        }
        res = validateValidRealm(newCustomRole);
        if (res != null) {
            auditSecEvent(uriInfo, "Failed to create custom role invalid realm specified.");
            return res;
        }

        String realmId = (String) newCustomRole.get("realmId");
        NeutralQuery existingCustomRoleQuery = new NeutralQuery();
        existingCustomRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.OPERATOR_EQUAL, realmId));
        Entity existingRoleDoc = repo.findOne(RESOURCE_NAME, existingCustomRoleQuery);
        if (existingRoleDoc != null) {
            auditSecEvent(uriInfo, "Failed to create custom role Already exists.");
            return buildBadRequest(ERROR_MULTIPLE_DOCS);
        }

        String id = service.create(newCustomRole);
        if (id != null) {
            String uri = uriToString(uriInfo) + "/" + id;
            auditSecEvent(uriInfo, "Created custom role with id: " + id);
            this.sessions.clear();
            return Response.status(Status.CREATED).header("Location", uri).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("{id}")
    @RightsAllowed({Right.CRUD_ROLE })
    public Response updateCustomRole(@PathParam("id") String id, EntityBody updated, @Context final UriInfo uriInfo) {

        Response res = validateRights(updated);
        if (res != null) {
            auditSecEvent(uriInfo, "Failed to create custom role rights validation failed.");
            return res;
        }
        res = validateUniqueRoles(updated);
        if (res != null) {
            auditSecEvent(uriInfo, "Failed to create custom role unique roles check failed.");
            return res;
        }
        res = validateValidRealm(updated);
        if (res != null) {
            auditSecEvent(uriInfo, "Failed to create custom role invalid realm specified.");
            return res;
        }

        EntityBody oldRealm = service.get(id);
        String oldRealmId = (String) oldRealm.get("realmId");
        String updatedRealmId = (String) updated.get("realmId");
        if (!updatedRealmId.equals(oldRealmId)) {
            auditSecEvent(uriInfo, "Failed to update realmId { from: " + oldRealmId + ", to: " + updatedRealmId
                    + " } for role with id:" + id);
            return buildBadRequest(ERROR_CHANGING_REALM_ID);
        }

        if (service.update(id, updated)) {
            auditSecEvent(uriInfo, "Updated role with id:" + id);
            this.sessions.clear();
            return Response.status(Status.NO_CONTENT).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    private static String uriToString(UriInfo uri) {
        return uri.getBaseUri() + uri.getPath().replaceAll("/$", "");
    }

    @DELETE
    @Path("{id}")
    @RightsAllowed({Right.CRUD_ROLE })
    public Response deleteCustomRole(@PathParam("id") String id, @Context final UriInfo uriInfo) {
        service.delete(id);
        auditSecEvent(uriInfo, "Deleted role with id:" + id);
        this.sessions.clear();
        return Response.status(Status.NO_CONTENT).build();
    }

    private Response validateRights(EntityBody customRoleDoc) {
        @SuppressWarnings("unchecked")
        List<Map<String, List<String>>> roles = (List<Map<String, List<String>>>) customRoleDoc.get("roles");
        for (Map<String, List<String>> cur : roles) {
            List<String> rights = cur.get("rights");
            Set<Right> rightsSet = new HashSet<Right>();
            for (String rightName : rights) {
                Right right = null;
                try {
                    right = Right.valueOf(rightName);
                } catch (IllegalArgumentException iae) {
                    return buildBadRequest(ERROR_INVALID_RIGHT);
                }

                if (rightsSet.contains(right)) {
                    return buildBadRequest(ERROR_DUPLICATE_RIGHTS);
                } else {
                    rightsSet.add(right);
                }
            }

        }
        return null;
    }

    private Response validateUniqueRoles(EntityBody customRoleDoc) {
        Set<String> roleNames = new HashSet<String>();
        @SuppressWarnings("unchecked")
        List<Map<String, List<String>>> roles = (List<Map<String, List<String>>>) customRoleDoc.get("roles");
        for (Map<String, List<String>> cur : roles) {
            List<String> names = cur.get("names");
            for (String name : names) {
                if (roleNames.contains(name)) {
                    return buildBadRequest(ERROR_DUPLICATE_ROLE);
                } else {
                    roleNames.add(name);
                }
            }
        }
        return null;
    }

    private Response validateValidRealm(EntityBody customRoleDoc) {
        Set<String> realmIds = realmHelper.getAssociatedRealmIds();
        if (!realmIds.contains(customRoleDoc.get("realmId"))) {
            return Response.status(Status.FORBIDDEN).entity(ERROR_INVALID_REALM).build();
        }
        return null;
    }

    private void auditSecEvent(UriInfo uriInfo, String message) {
        audit(securityEventBuilder.createSecurityEvent(CustomRoleResource.class.getName(), uriInfo.getRequestUri(), message));
    }

    private Response buildBadRequest(String message) {
        return Response.status(Status.BAD_REQUEST).entity(message).build();
    }

}
