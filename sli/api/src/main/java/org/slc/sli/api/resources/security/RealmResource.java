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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Realm role mapping API. Allows full CRUD on realm objects. Primarily intended to allow
 * mappings between SLI roles and client roles as realms should not be created or deleted
 * frequently.
 *
 * @author jnanney
 */
@Component
@Scope("request")
@Path("/realm")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class RealmResource {

    @Autowired
    private EntityDefinitionStore store;

    private EntityService service;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private IdConverter idConverter;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Autowired
    private RoleInitializer roleInitializer;

    @Autowired
    private RealmHelper realmHelper;


    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("realm");
        setService(def.getService());
    }

    // Injector
    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }

    // Injector
    public void setService(EntityService service) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{realmId}")
    @Consumes("application/json")
    public Response updateRealm(@PathParam("realmId") String realmId, EntityBody updatedRealm,
            @Context final UriInfo uriInfo) {
        SecurityUtil.ensureAuthenticated();
        if (!SecurityUtil.hasRight(Right.CRUD_REALM)) {
            return SecurityUtil.forbiddenResponse();
        }

        if (updatedRealm == null) {
            throw new EntityNotFoundException("Entity was null");
        }

        EntityBody oldRealm = service.get(realmId);

        if (!canEditCurrentRealm(updatedRealm) || oldRealm.get("edOrg") != null
                && !oldRealm.get("edOrg").equals(SecurityUtil.getEdOrg())) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to update this realm.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        Response validateUniqueness = validateUniqueId(realmId, (String) updatedRealm.get("uniqueIdentifier"), (String) updatedRealm.get("name"));
        if (validateUniqueness != null) {
            return validateUniqueness;
        }

        // set the tenant and edOrg
        updatedRealm.put("tenantId", SecurityUtil.getTenantId());
        updatedRealm.put("edOrg", SecurityUtil.getEdOrg());

        if (service.update(realmId, updatedRealm)) {
            audit(securityEventBuilder.createSecurityEvent(RealmResource.class.getName(), uriInfo, "Realm ["
                    + updatedRealm.get("name") + "] updated!"));
            return Response.status(Status.NO_CONTENT).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("{realmId}")
    public Response deleteRealm(@PathParam("realmId") String realmId, @Context final UriInfo uriInfo) {
        if (!SecurityUtil.hasRight(Right.CRUD_REALM)) {
            return SecurityUtil.forbiddenResponse();
        }
        EntityBody deletedRealm = service.get(realmId);
        service.delete(realmId);
        roleInitializer.dropRoles(realmId);
        audit(securityEventBuilder.createSecurityEvent(RealmResource.class.getName(), uriInfo, "Realm ["
                + deletedRealm.get("name") + "] deleted!"));
        return Response.status(Status.NO_CONTENT).build();
    }

    @POST
    @SuppressWarnings("unchecked")
    public Response createRealm(EntityBody newRealm, @Context final UriInfo uriInfo) {
        SecurityUtil.ensureAuthenticated();
        if (!SecurityUtil.hasRight(Right.CRUD_REALM)) {
            return SecurityUtil.forbiddenResponse();
        }

        if (!canEditCurrentRealm(newRealm)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to create a realm for another ed org");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        Response validateUniqueness = validateUniqueId(null, (String) newRealm.get("uniqueIdentifier"), (String) newRealm.get("name"));
        if (validateUniqueness != null) {
            debug("On realm create, uniqueId is not unique");
            return validateUniqueness;
        }

        // set the tenant and edOrg
        newRealm.put("tenantId", SecurityUtil.getTenantId());
        newRealm.put("edOrg", SecurityUtil.getEdOrg());

        String id = service.create(newRealm);

        //Also create custom roles
        roleInitializer.dropAndBuildRoles(id);

        audit(securityEventBuilder.createSecurityEvent(RealmResource.class.getName(), uriInfo, "Realm ["
                + newRealm.get("name") + "] created!"));
        String uri = uriToString(uriInfo) + "/" + id;

        return Response.status(Status.CREATED).header("Location", uri).build();
    }

    @GET
    @Path("{realmId}")
    public Response readRealm(@PathParam("realmId") String realmId) {
        SecurityUtil.ensureAuthenticated();
        EntityBody result = service.get(realmId);
        return Response.ok(result).build();
    }

    @GET
    public Response getRealms(@QueryParam("realm") @DefaultValue("") String realm, @Context UriInfo info) {
        SecurityUtil.ensureAuthenticated();
        
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, principal.getTenantId()));
        neutralQuery.addCriteria(new NeutralCriteria("edOrg", NeutralCriteria.OPERATOR_EQUAL, principal.getEdOrg()));
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(100);

        List<EntityBody> result = new ArrayList<EntityBody>();
        Iterable<String> realmList = service.listIds(neutralQuery);
        for (String id : realmList) {
            EntityBody curEntity = service.get(id);

            if (curEntity == null) {
                continue;
            }

            if (realm.length() == 0) {
                curEntity.put("link", info.getBaseUri() + info.getPath().replaceAll("/$", "") + "/" + id);
                result.add(curEntity);
            } else {
                if (realm.equals(curEntity.get("realm"))) {
                    result.add(curEntity);
                }
            }
        }
        return Response.ok(result).build();
    }


    private Response validateUniqueId(String realmId, String uniqueId, String displayName) {
        if (uniqueId == null || uniqueId.length() == 0) {
            return null;
        }
        // Check for uniqueness of Unique ID
        final NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("uniqueIdentifier", "=", uniqueId));
        if (realmId != null) {
            query.addCriteria(new NeutralCriteria("_id", "!=", idConverter.toDatabaseId(realmId)));
        }
        Entity body =
                SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {

                    @Override
                    public Entity execute() {
                        return repo.findOne("realm", query);
                    } });

        if (body != null) {
            debug("uniqueId: {}", body.getBody().get("uniqueIdentifier"));
            Map<String, String> res = new HashMap<String, String>();
            res.put("response", "Cannot have duplicate unique identifiers");
            return Response.status(Status.BAD_REQUEST).entity(res).build();
        }

        // Check for uniqueness of Display Name
        final NeutralQuery displayNameQuery = new NeutralQuery();
        displayNameQuery.addCriteria(new NeutralCriteria("name", "=", displayName));
        if (realmId != null) {
            displayNameQuery.addCriteria(new NeutralCriteria("_id", "!=", idConverter.toDatabaseId(realmId)));
        }
        Entity entity = SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {

            @Override
            public Entity execute() {
                return repo.findOne("realm", displayNameQuery);
            }
        });

        if (entity != null) {
            debug("name: {}", entity.getBody().get("name"));
            Map<String, String> res = new HashMap<String, String>();
            res.put("response", "Cannot have duplicate display names");
            return Response.status(Status.BAD_REQUEST).entity(res).build();
        }

        return null;
    }

    private static String uriToString(UriInfo uri) {
        return uri.getBaseUri() + uri.getPath().replaceAll("/$", "");
    }

    private boolean canEditCurrentRealm(EntityBody realm) {
        String edOrg = SecurityUtil.getEdOrg();
        return !(edOrg == null || !edOrg.equals(realm.get("edOrg")));

    }
}
