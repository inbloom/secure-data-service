package org.slc.sli.api.security.oauth;

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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
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
public class ApplicationService {

    @Autowired
    private EntityDefinitionStore store;

    private EntityService service;

    private static final String[] ATTRIBUTES_TO_HIDE = new String[] {
            "client_type", "redirect_uri", "name", "developer_info",
            "client_secret", "description" };

    private static final int CLIENT_ID_LENGTH = 10;
    private static final int CLIENT_SECRET_LENGTH = 48;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("application");
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

    @POST
    public Response createApplication(EntityBody newApp) {
        String newToken = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        while (isDuplicateToken(newToken)) {
            newToken = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        }

        newApp.put("client_id", newToken);
        newApp.put("client_secret",
                TokenGenerator.generateToken(CLIENT_SECRET_LENGTH));
        String id = service.create(newApp);
        EntityBody resObj = new EntityBody();
        resObj.put("id", id);

        return Response.status(Status.CREATED).entity(resObj).build();
    }

    private boolean isDuplicateToken(String token) {
        return (service.list(0, 1, "client_id=" + token)).iterator().hasNext();
    }

    @GET
    public List<EntityBody> getApplications(@Context UriInfo info) {
        List<EntityBody> results = new ArrayList<EntityBody>();
        Iterable<String> realmList = service.list(0, 1000);
        for (String id : realmList) {
            EntityBody result = service.get(id);
            for (String toRemove : ATTRIBUTES_TO_HIDE) {
                result.remove(toRemove);
            }
            result.put("link",
                    info.getBaseUri() + info.getPath().replaceAll("/$", "")
                            + "/" + result.get("client_id"));
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
    @Path("{client_id}")
    public Response getApplication(@PathParam("client_id") String clientId) {
        Iterable<String> results = service.list(0, 1, "client_id=" + clientId);

        for (String id : results) {
            return Response.status(Status.OK).entity(service.get(id)).build();
        }

        return Response.status(Status.NOT_FOUND).build();

    }

    @DELETE
    @Path("{client_id}")
    public Response deleteApplication(@PathParam("client_id") String clientId) {
        Iterable<String> results = service.list(0, 1, "client_id=" + clientId);

        for (String id : results) {
            service.delete(id);
            return Response.status(Status.NO_CONTENT).build();
        }

        return Response.status(Status.NOT_FOUND).build();
    }
}
