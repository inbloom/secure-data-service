package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.oauth.TokenGenerator;
import org.slc.sli.api.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * Implements the ClientDetailsService interface provided by the Spring OAuth
 * 2.0 implementation.
 * 
 * @author shalka
 */
@Component
@Scope("request")
@Path("/apps")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ApplicationResource {

    @Autowired
    private EntityDefinitionStore store;

    private EntityService service;

    private static final int CLIENT_ID_LENGTH = 10;
    private static final int CLIENT_SECRET_LENGTH = 48;
    public static final String CLIENT_ID = "client_id";

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("application");
        this.service = def.getService();
    }

    @POST
    public Response createApplication(EntityBody newApp) {
        String clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        while (isDuplicateToken(clientId)) {
            clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        }

        newApp.put(CLIENT_ID, clientId);
        
        String clientSecret = TokenGenerator.generateToken(CLIENT_SECRET_LENGTH);
        newApp.put("client_secret", clientSecret);
        String id = service.create(newApp);
        
        EntityBody resObj = new EntityBody();
        resObj.put(id, id);
        resObj.put(CLIENT_ID, clientId);
        resObj.put("client_secret", clientSecret);
        return Response.status(Status.CREATED).entity(resObj).build();
    }

    private boolean isDuplicateToken(String token) {
        return (service.list(0, 1, CLIENT_ID + "=" + token)).iterator().hasNext();
    }

    @GET
    public List<EntityBody> getApplications(@Context UriInfo info) {
        List<EntityBody> results = new ArrayList<EntityBody>();
        Iterable<String> realmList = service.list(0, 1000);
        for (String id : realmList) {
            EntityBody result = service.get(id);

            result.put("link",
                    info.getBaseUri() + info.getPath().replaceAll("/$", "")
                            + "/" + result.get(CLIENT_ID));
            results.add(result);
        }
        return results;
    }

    /**
     * Looks up a specific application based on client ID, ie.
     * /api/rest/apps/<client_id>
     * 
     * @param clientId
     *            the client ID, not the "id"
     * @return the JSON data of the application, otherwise 404 if not found
     */
    @GET
    @Path("{" + CLIENT_ID + "}")
    public Response getApplication(@PathParam(CLIENT_ID) String clientId) {
        String uuid = lookupIdFromClientId(clientId);

        if (uuid != null) {
            return Response.status(Status.OK).entity(service.get(uuid)).build();
        }

        return Response.status(Status.NOT_FOUND).build();

    }

    @DELETE
    @Path("{" + CLIENT_ID + "}")
    public Response deleteApplication(@PathParam(CLIENT_ID) String clientId) {
        
        String uuid = lookupIdFromClientId(clientId);
        if (uuid != null) {
            service.delete(uuid);
            return Response.status(Status.NO_CONTENT).build();
        }

        return Response.status(Status.NOT_FOUND).build();
    }
    
    /**
     * Since entries are keyed off a uuid instead of the client ID,
     * we need to look up the uuid using the client id.
     * 
     * @param clientId
     * @return a uuid, or null if not found
     */
    private String lookupIdFromClientId(String clientId) {
        Iterable<String> results = service.list(0, 1, CLIENT_ID + "=" + clientId);
        if (results.iterator().hasNext()) {
            return results.iterator().next();
        }
        return null;
    }

}
