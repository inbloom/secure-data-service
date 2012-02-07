package org.slc.sli.api.resources.security;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;

/**
 * Realm role mapping API. Allows full CRUD on realm objects.  Primarily intended to allow
 * mappings between SLI roles and client roles as realms should not be created or deleted
 * frequently.
 *
 * @author jnanney
 *
 */
@Component
@Scope("request")
@Path("/realm")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class RealmRoleManagerResource {

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private RoleRightAccess roleRightAccess;

    private EntityService service;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("realm");
        setService(def.getService());
    }

    //Injector
    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }

    //Injector
    public void setService(EntityService service) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{realmId}")
    @Consumes("application/json")
    public Response updateClientRole(@PathParam("realmId") String realmId, EntityBody updatedRealm) {
        if (updatedRealm == null) {
            throw new EntityNotFoundException("Entity was null");
        }
        Map<String, List<String>> mappings = (Map<String, List<String>>) updatedRealm.get("mappings");
        if (mappings != null) {
            Response validateResponse = validateMappings(mappings);
            if (validateResponse != null) {
                return validateResponse;
            }
        }
        if (service.update(realmId, updatedRealm)) {
            return Response.status(Status.NO_CONTENT).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("{realmId}")
    public Response deleteRealm(@PathParam("realmId") String realmId) {
        service.delete(realmId);
        return Response.status(Status.NO_CONTENT).build();
    }

    @POST
    @SuppressWarnings("unchecked")
    public Response createRealm(EntityBody newRealm) {
        Map<String, List<String>> mappings = (Map<String, List<String>>) newRealm.get("mappings");
        if (mappings != null) {
            Response validateResponse = validateMappings(mappings);

            if (validateResponse != null) {
                return validateResponse;
            }
        }

        String id = service.create(newRealm);
        if (id != null) {
            service.create(newRealm);
        }
        EntityBody resObj = new EntityBody();
        resObj.put("id", id);
        return Response.status(Status.CREATED).entity(resObj).build();
    }

    @GET
    @Path("{realmId}")
    public EntityBody getMappings(@PathParam("realmId") String realmId) {
        EntityBody result = service.get(realmId);
        if (result != null && result.get("mappings") == null) {
            result.put("mappings", new HashMap<String, List<String>>());
        }
        return result;
    }

    @GET
    public List<EntityBody> getRealms(@Context UriInfo info) {
        List<EntityBody> result = new ArrayList<EntityBody>();
        Iterable<String> realmList = service.list(0, 100);
        for (String id : realmList) {
            EntityBody curEntity = getMappings(id);
            curEntity.remove("mappings");
            curEntity.put("link", info.getBaseUri() + info.getPath().replaceAll("/$", "") + "/" + id);
            result.add(curEntity);
        }
        return result;
    }

    private boolean uniqueMappings(Map<String, List<String>> mappings) {
        Set<String> clientRoles = new HashSet<String>();
        for (String sliRole : mappings.keySet()) {
            List<String> clientRolesForSliRole = mappings.get(sliRole);
            for (String clientRole : clientRolesForSliRole) {
                if (clientRoles.contains(clientRole)) {
                    return false;
                }
                clientRoles.add(clientRole);
            }
        }
        return true;
    }

    private Response validateMappings(Map<String, List<String>> mappings) {
        HashMap<String, String> res = new HashMap<String, String>();
        if (!uniqueMappings(mappings)) {
            res.put("response", "Client have duplicate client roles");
            return Response.status(Status.BAD_REQUEST).entity(res).build();
         }

        for (String sliRole : mappings.keySet()) {
            if (roleRightAccess.getDefaultRole(sliRole) == null) {
                res.put("response", "Invalid SLI Role");
                return Response.status(Status.BAD_REQUEST).build();
            }

            for (String clientRole : mappings.get(sliRole)) {
                if (clientRole.length() == 0) {
                    res.put("response", "Cannot have client role of length 0");
                    return Response.status(Status.BAD_REQUEST).entity(res).build();
                }
            }
        }
        return null;
    }

}
