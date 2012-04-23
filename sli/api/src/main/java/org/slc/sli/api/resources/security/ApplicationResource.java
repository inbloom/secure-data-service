package org.slc.sli.api.resources.security;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.security.oauth.TokenGenerator;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * Provides CRUD operations on registered application through the /apps path.
 *
 * @author shalka
 */
@Component
@Scope("request")
@Path("apps")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ApplicationResource extends DefaultCrudEndpoint {

    @Autowired
    private EntityDefinitionStore store;

    private EntityService service;

    private static final int CLIENT_ID_LENGTH = 10;
    private static final int CLIENT_SECRET_LENGTH = 48;
    private static final String REGISTERED = "registered";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String RESOURCE_NAME = "application";
    public static final String UUID = "uuid";
    public static final String LOCATION = "Location";
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationResource.class);

//    @PostConstruct
//    public void init() {
//        EntityDefinition def = store.lookupByResourceName(RESOURCE_NAME);
//        this.service = def.getService();
//    }

    @Autowired
    public ApplicationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        store = entityDefs;
        service = store.lookupByResourceName(RESOURCE_NAME).getService();
    }

    @POST
    public Response createApplication(EntityBody newApp, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        String clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        while (isDuplicateToken(clientId)) {
            clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        }

        if (newApp.containsKey(CLIENT_SECRET)
                || newApp.containsKey(CLIENT_ID)
 || newApp.containsKey("id")) {
            EntityBody body = new EntityBody();
            body.put("message", "Auto-generated attribute (id|client_secret|client_id) specified in POST.  "
            + "Remove attribute and try again.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        newApp.put(CLIENT_ID, clientId);
        newApp.put(REGISTERED, true);

        String clientSecret = TokenGenerator.generateToken(CLIENT_SECRET_LENGTH);
        newApp.put(CLIENT_SECRET, clientSecret);
        return super.create(newApp, headers, uriInfo);
    }

    private boolean isDuplicateToken(String token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria(CLIENT_ID + "=" + token));
        try {
            return (service.list(neutralQuery)).iterator().hasNext();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    @GET
    public Response getApplications(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
                                            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
                                            @Context
                                            HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    private static String uriToString(UriInfo uri) {
        return uri.getBaseUri() + uri.getPath().replaceAll("/$", "");
    }

    /**
     * Looks up a specific application based on client ID, ie.
     * /api/rest/apps/<uuid>
     *
     * @param uuid
     *            the client ID, not the "id"
     * @return the JSON data of the application, otherwise 404 if not found
     */
    @GET
    @Path("{" + UUID + "}")
    public Response getApplication(@PathParam(UUID) String uuid,
                                   @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(uuid, headers, uriInfo);
    }

    @DELETE
    @Path("{" + UUID + "}")
    public Response deleteApplication(@PathParam(UUID) String uuid,
                                      @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        return super.delete(uuid, headers, uriInfo);
    }

    @PUT
    @Path("{" + UUID + "}")
    public Response updateApplication(@PathParam(UUID) String uuid, EntityBody app,
                                      @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        EntityBody oldApp = service.get(uuid);
        String clientSecret = (String) app.get(CLIENT_SECRET);
        String clientId = (String) app.get(CLIENT_ID);
        String id = (String) app.get("id");

        if ((clientSecret != null && !clientSecret.equals(oldApp.get(CLIENT_SECRET)))
                || (clientId != null && !clientId.equals(oldApp.get(CLIENT_ID)))
                || (id != null && !id.equals(oldApp.get("id")))) {
            EntityBody body = new EntityBody();
            body.put("message", "Cannot modify attribute (id|client_secret|client_id) specified in PUT.  "
            + "Remove attribute and try again.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();

        }

        return super.update(uuid, app, headers, uriInfo);
    }
}
