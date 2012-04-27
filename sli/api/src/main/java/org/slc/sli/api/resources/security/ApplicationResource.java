package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    
    @Autowired
    @Value("${sli.sandbox.autoRegisterApps}")
    private boolean autoRegister;

    private EntityService service;

    private static final int CLIENT_ID_LENGTH = 10;
    private static final int CLIENT_SECRET_LENGTH = 48;
    public static final String REGISTRATION = "registration";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String APPROVAL_DATE = "approval_date";
    public static final String REQUEST_DATE = "request_date";
    public static final String STATUS = "status";
    public static final String RESOURCE_NAME = "application";
    public static final String UUID = "uuid";
    public static final String LOCATION = "Location";


    public void setAutoRegister(boolean register) {
        this.autoRegister = register;
    }

    private boolean hasRight(Right required) {
        Collection<GrantedAuthority> rights = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return rights.contains(required);
    }

    @Autowired
    public ApplicationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        store = entityDefs;
        service = store.lookupByResourceName(RESOURCE_NAME).getService();
    }

    @POST
    public Response createApplication(EntityBody newApp, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        if (newApp.containsKey(CLIENT_SECRET)
                || newApp.containsKey(CLIENT_ID)
                || newApp.containsKey("id")) {
            EntityBody body = new EntityBody();
            body.put("message", "Auto-generated attribute (id|client_secret|client_id) specified in POST.  "
                    + "Remove attribute and try again.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        if (!hasRight(Right.APP_CREATION)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to create new applications.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        String clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        while (isDuplicateToken(clientId)) {
            clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        }
        
        newApp.put(CLIENT_ID, clientId);

        Map<String, Object> registration = new HashMap<String, Object>();
        registration.put(STATUS, "PENDING");
        if (autoRegister) {
            registration.put(APPROVAL_DATE, System.currentTimeMillis());
            registration.put(STATUS, "APPROVED");
            registration.put(APPROVAL_DATE, System.currentTimeMillis());
        }
        registration.put(REQUEST_DATE, System.currentTimeMillis());
        newApp.put(REGISTRATION, registration);
        
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

    @SuppressWarnings("rawtypes")
    @GET
    public Response getApplications(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context
            HttpHeaders headers, @Context final UriInfo uriInfo) {
        Response resp = super.readAll(offset, limit, headers, uriInfo);
        filterSensitiveData((Map) resp.getEntity());
        return resp;
    }

    /**
     * Looks up a specific application based on client ID, ie.
     * /api/rest/apps/<uuid>
     *
     * @param uuid
     *            the client ID, not the "id"
     * @return the JSON data of the application, otherwise 404 if not found
     */
    @SuppressWarnings("rawtypes")
    @GET
    @Path("{" + UUID + "}")
    public Response getApplication(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        Response resp =  super.read(uuid, headers, uriInfo);
        filterSensitiveData((Map) resp.getEntity());
        return resp;
    }

    /**
     * If an app hasn't been approved, the client_id and client_secret shouldn't
     * be visible to the developer.
     * 
     * @param entity
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void filterSensitiveData(Map entity) {
        Object appObj = entity.get("application");
        List appList = new ArrayList();
        if (appObj != null) {
            if (appObj instanceof Map) {
                appList.add(appObj);
            } else if (appObj instanceof List) {
                appList = (List) appObj;
            }
            
            for (Object app : appList) {
                Map appMap = (Map) app;
                Map reg = (Map) appMap.get("registration");
                if (!reg.get("status").equals("APPROVED")) {
                    appMap.remove(CLIENT_ID);
                    appMap.remove(CLIENT_SECRET);
                }
            }
        }
    }

    @DELETE
    @Path("{" + UUID + "}")
    public Response deleteApplication(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {


        return super.delete(uuid, headers, uriInfo);
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{" + UUID + "}")
    public Response updateApplication(@PathParam(UUID) String uuid, EntityBody app,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        EntityBody oldApp = service.get(uuid);
        
        //The client id and secret could be null if they were filtered from the client
        String clientSecret = (String) app.get(CLIENT_SECRET);
        if (clientSecret == null)
            app.put(CLIENT_SECRET, oldApp.get(CLIENT_SECRET));
        String clientId = (String) app.get(CLIENT_ID);
        if (clientId == null)
            app.put(CLIENT_ID, oldApp.get(CLIENT_ID));
        
        String id = (String) app.get("id");
        Map<String, Object> oldReg = ((Map<String, Object>) oldApp.get(REGISTRATION));
        Map<String, Object> newReg = ((Map<String, Object>) app.get(REGISTRATION));
        String newRegStatus = (String) newReg.get(STATUS);
        String oldRegStatus = (String) oldReg.get(STATUS);
        List<String> changedKeys = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : app.entrySet()) {
            if (oldApp.containsKey(entry.getKey()) && !oldApp.get(entry.getKey()).equals(entry.getValue())) {
                changedKeys.add(entry.getKey());
            }
        }

        if ((clientSecret != null && !clientSecret.equals(oldApp.get(CLIENT_SECRET)))
                || (clientId != null && !clientId.equals(oldApp.get(CLIENT_ID)))
                || (id != null && !id.equals(oldApp.get("id")))
                || (!registrationDatesMatch(oldReg, newReg, APPROVAL_DATE))
                || (!registrationDatesMatch(oldReg, newReg, REQUEST_DATE))) {
            EntityBody body = new EntityBody();
            body.put("message", "Cannot modify attribute (id|client_secret|client_id|request_date|approval_date) specified in PUT.  "
                    + "Remove attribute and try again.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        
        changedKeys.remove("registration");
        changedKeys.remove("developer_info");   //TODO: developer_info is a pain since it's nested--need to validate this hasn't changed
        changedKeys.remove("metaData");
        
        //Operator - can only change registration status
        if (hasRight(Right.APP_REGISTER)) {
            if (changedKeys.size() > 0) {
                EntityBody body = new EntityBody();
                body.put("message", "You are not authorized to alter applications.");
                return Response.status(Status.BAD_REQUEST).entity(body).build();
            }
            
            if (newRegStatus.equals("APPROVED") && oldRegStatus.equals("PENDING")) {
                debug("App approved");
                newReg.put(STATUS, "APPROVED");
                newReg.put(APPROVAL_DATE, System.currentTimeMillis());
            } else if (newRegStatus.equals("DENIED") && oldRegStatus.equals("PENDING")) {
                debug("App denied");
            } else if (newRegStatus.equals("UNREGISTERED") && oldRegStatus.equals("APPROVED")) { 
                debug("App unregistered");
                newReg.remove(APPROVAL_DATE);
                newReg.remove(REQUEST_DATE);
            } else {
                EntityBody body = new EntityBody();
                body.put("message", "Invalid state change: " + oldRegStatus + " to " + newRegStatus);
                return Response.status(Status.BAD_REQUEST).entity(body).build();
            }
                        
        } else if (hasRight(Right.APP_CREATION)) {  //App Developer
            if (!oldRegStatus.endsWith(newRegStatus)) {
                EntityBody body = new EntityBody();
                body.put("message", "You are not authorized to register applications.");
                return Response.status(Status.BAD_REQUEST).entity(body).build();
            }
            
            if (oldRegStatus.equals("PENDING")) {
                EntityBody body = new EntityBody();
                body.put("message", "Application cannot be modified while approval request is in Pending state.");
                return Response.status(Status.BAD_REQUEST).entity(body).build();
            }
            
            //when a denied or unreg'ed app is altered, it goes back into pending
            if (oldRegStatus.equals("DENIED") || oldRegStatus.equals("UNREGISTERED")) {
                
                //TODO: If auto approval is on, approve instead
                newReg.put(STATUS, "PENDING");
                newReg.put(REQUEST_DATE, System.currentTimeMillis());
            }
        } else {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to update application.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        
        return super.update(uuid, app, headers, uriInfo);
    }

    /**
     * Compares two date fields on the registration object.
     * 
     * It's only tricky because we one or both could be null
     * e.g. an app in pending state won't have an approval date field
     * 
     * @param oldReg
     * @param newReg
     * @param field either request_date or approval_date
     * @return
     */
    private boolean registrationDatesMatch(Map<String, Object> oldReg,
            Map<String, Object> newReg, String field) {
        Long oldDate = (Long) oldReg.get(field);
        Long newDate = (Long) newReg.get(field);
        if (oldDate == null && newDate != null)
            return false;
        if (newDate == null && oldDate != null)
            return false;
        return (oldDate == newDate || oldDate.equals(newDate));
    }

}
