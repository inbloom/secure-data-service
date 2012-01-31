package org.slc.sli.api.resources.security;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Realm role mapping API. Allows a user to define mappings between SLI roles
 * and client roles.
 * 
 * @author jnanney
 * 
 */
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ClientRoleManagerResource {

    @Autowired
    private EntityDefinitionStore store;
    
    private EntityService service;

    @PostConstruct
    private void init() {
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
    @RequestMapping("/realms/{realmId}")
    public boolean updateClientRole(@PathVariable("realmId") String realmId, EntityBody updatedRealm) {
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
    @RequestMapping("/realms/{realmId}")
    public Object getMappings(@PathVariable("realmId") String realmId) {
        return service.get(realmId);
    }
}
