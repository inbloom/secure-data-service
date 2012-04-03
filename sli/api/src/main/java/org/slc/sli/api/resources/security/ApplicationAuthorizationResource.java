package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 
 * @author pwolf
 *
 */
@Component
@Scope("request")
@Path("/applicationAuthorization")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ApplicationAuthorizationResource {

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    Repository<Entity> repo;

    private EntityService service;

    public static final String RESOURCE_NAME = "applicationAuthorization"; 
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationAuthorizationResource.class);

    public static final String UUID = "uuid";
    public static final String AUTH_ID = "authId";
    public static final String AUTH_TYPE = "authType";
    public static final String EDORG_AUTH_TYPE = "EDUCATION_ORGANIZATION";

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(RESOURCE_NAME);
        this.service = def.getService();
    }

    @GET
    @Path("{" + UUID + "}")
    public Response getAuthorizations(@PathParam(UUID) String uuid) {

        if (uuid != null) {

            EntityBody entityBody = service.get(uuid);
            if (entityBody != null) {
                verifyAccess((String) entityBody.get(AUTH_ID));
                return Response.status(Status.OK).entity(entityBody).build();

            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    public Response createAuthorization(EntityBody newAppAuth, @Context final UriInfo uriInfo) {
        verifyAccess((String) newAppAuth.get(AUTH_ID));        
        String uuid = service.create(newAppAuth);
        String uri = uriToString(uriInfo) + "/" + uuid;
        return Response.status(Status.CREATED).header("Location", uri).build();
    }

    @PUT
    @Path("{" + UUID + "}") 
    public Response updateAuthorization(@PathParam(UUID) String uuid, EntityBody auth) {
        EntityBody oldAuth = service.get(uuid);
        verifyAccess((String) oldAuth.get(AUTH_ID));

        if (!oldAuth.get(AUTH_ID).equals(auth.get(AUTH_ID))) {
            EntityBody body = new EntityBody();
            body.put("message", "authId is read only");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        if (!oldAuth.get(AUTH_TYPE).equals(auth.get(AUTH_TYPE))) {
            EntityBody body = new EntityBody();
            body.put("message", "authType is read only");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        boolean status = service.update(uuid, auth);
        if (status) {
            return Response.status(Status.NO_CONTENT).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    @GET
    public List<Map<String, Object>> getAuthorizations(@Context UriInfo info) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        String edOrg = getUsersStateUniqueId();
        if (edOrg != null) {
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria(AUTH_TYPE, "=", EDORG_AUTH_TYPE));
            query.addCriteria(new NeutralCriteria(AUTH_ID, "=", edOrg));
            Entity ent = repo.findOne(RESOURCE_NAME, query);
            if (ent != null) {
                ent.getBody().put("link", uriToString(info) + "/" + ent.getEntityId());
                ent.getBody().put("id", ent.getEntityId());
                results.add(ent.getBody());
            }

        }
        return results;
    }

    private String getUsersStateUniqueId() {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
            return principal.getEdOrg();
        }
        return null;
    }

    private static String uriToString(UriInfo uri) {
        return uri.getBaseUri() + uri.getPath().replaceAll("/$", "");
    }

    private void verifyAccess(String authId) throws AccessDeniedException {
        String edOrg = getUsersStateUniqueId();
        if (edOrg == null) {
            throw new InsufficientAuthenticationException("No edorg exists on principal.");
        }
        if (!edOrg.equals(authId)) {
            throw new AccessDeniedException("User can only access " + edOrg);
        }
    }
}
