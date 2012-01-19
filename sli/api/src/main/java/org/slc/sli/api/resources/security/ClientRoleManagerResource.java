package org.slc.sli.api.resources.security;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.resolve.ClientRoleManager;

@Component
@Path("/pub/roles")
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ClientRoleManagerResource {
    
    @Autowired
    private ClientRoleManager roleManager;
    
    @GET
    @Path("addClientRole")
    public void addClientRole(@QueryParam("realmId") String realmId, @QueryParam("sliRole") String sliRoleName,
            @QueryParam("clientRole") String clientRoleName) {
        roleManager.addClientRole(realmId, sliRoleName, clientRoleName);
    }
    
    @GET
    @Path("deleteClientRole")
    public void deleteClientRole(@QueryParam("realmId") String realmId, @QueryParam("clientRole") String clientRoleName) {
        roleManager.deleteClientRole(realmId, clientRoleName);
    }
    
    @GET
    @Path("mappings")
    public Object getMappings(@QueryParam("realmId") String realmId, @QueryParam("sliRole") String sliRoleName) {
        return roleManager.getMappings(realmId, sliRoleName);
    }
    
    @GET
    @Path("getSliRole")
    public String getSliRoleName(@QueryParam("realmId") String realmId, @QueryParam("clientRole") String clientRoleName) {
        return roleManager.getSliRoleName(realmId, clientRoleName);
        
    }
    
}
