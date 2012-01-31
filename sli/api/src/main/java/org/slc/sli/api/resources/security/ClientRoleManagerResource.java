package org.slc.sli.api.resources.security;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
@Path("/realms")
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ClientRoleManagerResource {

    @Autowired
    private EntityDefinitionStore store;
    
    private EntityService service;

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
    public boolean addClientRole(@PathVariable("realmId") String realmId, String id, EntityBody updatedRealm) {
        return service.update(id, updatedRealm);
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
        //TODO This should be a query.
        Iterable<String> ids = service.list(0, 100);
        Iterable<EntityBody> mappings = service.get(ids);
        for (EntityBody body : mappings) {
            if (body.get("realm_name").equals(realmId)) {
                return mappings;
            }
        }
        return null;
    }
}
