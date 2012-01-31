package org.slc.sli.api.resources.security;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.service.EntityService;

/**
 * Realm role mapping API. Allows a user to define mappings between SLI roles
 * and client roles.
 * 
 * @author jnanney
 * 
 */
@Component
@Scope("request")
@Path("/realms")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ClientRoleManagerResource {

    @Autowired
    private EntityDefinitionStore store;
    
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

    @PUT
    @Path("{realmId}")
    public boolean updateClientRole(@PathParam("realmId") String realmId, EntityBody updatedRealm) {
        return service.update(realmId, updatedRealm);
    }

//    @DELETE
//    @RequestMapping("/realms/{realmId}")
//    public Response deleteClientRole(@PathVariable("realmId") String realmId,
//            String clientRoleName, String sliRoleName) {
//        try {
//            if (roleManager.deleteClientRole(realmId, clientRoleName)) {
//                return Response.ok().build();
//            }
//        } catch (RealmRoleMappingException e) {
//            return Response.status(Status.FORBIDDEN).entity(e.getMessage())
//                    .build();
//        }
//        return Response.status(Status.NOT_FOUND).build();
//    }

    @GET
    @Path("{realmId}")
    public EntityBody getMappings(@PathParam("realmId") String realmId) {
        return service.get(realmId);
    }
    
    @GET
    public List<EntityBody> getRealms(@Context UriInfo info) {
        List<EntityBody> result = new ArrayList<EntityBody>();
        Iterable<String> realmList = service.list(0, 100);
        for (String id : realmList) {
            EntityBody curEntity = getMappings(id);
            curEntity.remove("mappings");
            curEntity.put("link", info.getRequestUri() + "/" + id);
            result.add(curEntity);
        }
        return result;
    }

}
