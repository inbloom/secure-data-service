package org.slc.sli.api.resources.security;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.resolve.ClientRoleManager;
import org.slc.sli.api.security.resolve.RealmRoleMappingException;

/**
 * Realm role mapping API. Allows a user to define mappings between SLI roles
 * and client roles.
 * 
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
    public Response addClientRole(@QueryParam("realmId") String realmId,
            @QueryParam("sliRole") String sliRoleName,
            @QueryParam("clientRole") String clientRoleName) {
        try {
            if (roleManager.addClientRole(realmId, sliRoleName, clientRoleName)) {
                return Response.ok().build();
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
        } catch (RealmRoleMappingException e) {
            return Response.status(Status.FORBIDDEN).entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    public Response deleteClientRole(@QueryParam("realmId") String realmId,
            @QueryParam("clientRole") String clientRoleName) {
        try {
            if (roleManager.deleteClientRole(realmId, clientRoleName)) {
                return Response.ok().build();
            }
        } catch (RealmRoleMappingException e) {
            return Response.status(Status.FORBIDDEN).entity(e.getMessage())
                    .build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    public Object getMappings(@QueryParam("realmId") String realmId,
            @QueryParam("sliRole") String sliRoleName) {
        return roleManager.getMappings(realmId, sliRoleName);
    }
}
