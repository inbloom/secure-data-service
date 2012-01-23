package org.slc.sli.api.resources.security;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.resolve.ClientRoleManager;

/**
 * Realm role mapping API.  Allows a user to definine mappings between SLI roles and
 * client roles. 
 * @author jnanney
 *
 */
@Component
@Path("/pub/roles/mappings")
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ClientRoleManagerResource {
    
    @Autowired
    private ClientRoleManager roleManager;
    
    @POST
    public void addClientRole(@QueryParam("realmId") String realmId, @QueryParam("sliRole") String sliRoleName,
            @QueryParam("clientRole") String clientRoleName) {
        roleManager.addClientRole(realmId, sliRoleName, clientRoleName);
    }
    
    @DELETE
    public void deleteClientRole(@QueryParam("realmId") String realmId, @QueryParam("clientRole") String clientRoleName) {
        roleManager.deleteClientRole(realmId, clientRoleName);
    }
    
    @GET
    public Object getMappings(@QueryParam("realmId") String realmId, @QueryParam("sliRole") String sliRoleName) {
        return roleManager.getMappings(realmId, sliRoleName);
    }
    
    @GET
    @Path("getSliRoleName")
    public String getSliRoleName(@QueryParam("realmId") String realmId, @QueryParam("clientRole") String clientRoleName) {
        return roleManager.getSliRoleName(realmId, clientRoleName);
        
    }
    
}
