package org.slc.sli.api.security.oauth;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Responsible for facilitating the OAuth 2.0 workflow. - user authentication -
 * application authentication - application authorization (future
 * implementation)
 * 
 * @author shalka
 */
@Component
@Scope("request")
@Path("/oauth")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class OAuthProvider {
    
    // @Autowired
    // private EntityDefinitionStore store;
    
    // private EntityService service;
    
    @GET
    @Path("authorize")
    public Response authorizeApplication(EntityBody newApp) {
        EntityBody response = new EntityBody();
        return Response.status(Status.CREATED).entity(response).build();
    }
    
    @POST
    @Path("token")
    public Response createAccessToken() {
        EntityBody response = new EntityBody();
        return Response.status(Status.CREATED).entity(response).build();
    }
}
