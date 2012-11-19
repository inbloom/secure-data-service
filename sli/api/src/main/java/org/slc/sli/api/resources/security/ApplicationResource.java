/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.resources.security;

import java.util.ArrayList;
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
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.TokenGenerator;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
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
@Produces({ HypermediaType.JSON + ";charset=utf-8" })
public class ApplicationResource extends DefaultCrudEndpoint {

    public static final String AUTHORIZED_ED_ORGS = "authorized_ed_orgs";

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    @Value("${sli.autoRegisterApps}")
    private boolean autoRegister;

    @Autowired
    @Value("${sli.sandbox.enabled}")
    private boolean sandboxEnabled;

    private EntityService service;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

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

    private static final String CREATED_BY = "created_by";

    //These fields can only be set during bootstrapping and can never be modified through the API
    private static final String[] PERMANENT_FIELDS = new String[] {"bootstrap", "authorized_for_all_edorgs", "allowed_for_all_edorgs", "admin_visible"};
    
    public void setAutoRegister(boolean register) {
        autoRegister = register;
    }

    @Autowired
    public ApplicationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        store = entityDefs;
        service = store.lookupByResourceName(RESOURCE_NAME).getService();
    }

    @POST
    public Response createApplication(EntityBody newApp, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        if (newApp.containsKey(CLIENT_SECRET) || newApp.containsKey(CLIENT_ID) || newApp.containsKey("id")) {
            EntityBody body = new EntityBody();
            body.put("message", "Auto-generated attribute (id|client_secret|client_id) specified in POST.  "
                    + "Remove attribute and try again.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        if (!SecurityUtil.hasRight(Right.DEV_APP_CRUD)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to create new applications.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        if (!missingRequiredUrls(newApp)) {
            EntityBody body = new EntityBody();
            body.put("message", "Applications that are not marked as installed must have a application url and redirect url");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        // Destroy the ed-orgs
        newApp.put(AUTHORIZED_ED_ORGS, new ArrayList<String>());

        String clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        while (isDuplicateToken(clientId)) {
            clientId = TokenGenerator.generateToken(CLIENT_ID_LENGTH);
        }

        newApp.put(CLIENT_ID, clientId);
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        newApp.put(CREATED_BY, principal.getExternalId());

        Map<String, Object> registration = new HashMap<String, Object>();
        registration.put(STATUS, "PENDING");
        if (autoRegister) {
            registration.put(APPROVAL_DATE, System.currentTimeMillis());
            registration.put(STATUS, "APPROVED");
        }
        registration.put(REQUEST_DATE, System.currentTimeMillis());
        newApp.put(REGISTRATION, registration);

        String clientSecret = TokenGenerator.generateToken(CLIENT_SECRET_LENGTH);
        newApp.put(CLIENT_SECRET, clientSecret);

        // we don't allow certain fields to be set through API
        for (String fieldName : PERMANENT_FIELDS) {
            newApp.remove(fieldName);
        }

        return super.create(newApp, headers, uriInfo);
    }

    private boolean isDuplicateToken(String token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria(CLIENT_ID + "=" + token));
        Iterable<EntityBody> it = service.list(neutralQuery);
       
        if (it == null || it.iterator() == null) {
        	return false;
        }
      
        return it.iterator().hasNext();
    }

    @SuppressWarnings("rawtypes")
    @GET
    public Response getApplications(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        Response resp = super.readAll(offset, limit, headers, uriInfo);
        filterSensitiveData((Map) resp.getEntity());
        return resp;
    }



    @Override
    protected void addAdditionalCritera(NeutralQuery query) {

        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (SecurityUtil.hasRight(Right.DEV_APP_CRUD)) { //Developer sees all apps they own
            query.addCriteria(new NeutralCriteria(CREATED_BY, NeutralCriteria.OPERATOR_EQUAL, principal.getExternalId()));
        } else if (!SecurityUtil.hasRight(Right.SLC_APP_APPROVE)) {  //realm admin, sees apps that they are either authorized or could be authorized

            //know this is ugly, but having trouble getting or queries to work
            List<String> idList = new ArrayList<String>();
            NeutralQuery newQuery = new NeutralQuery(new NeutralCriteria(AUTHORIZED_ED_ORGS, NeutralCriteria.OPERATOR_EQUAL, principal.getEdOrgId()));
            Iterable<String> ids = repo.findAllIds("application", newQuery);
            for (String id : ids) {
                idList.add(id);
            }

            newQuery = new NeutralQuery(0);
            newQuery.addCriteria(new NeutralCriteria("allowed_for_all_edorgs", NeutralCriteria.OPERATOR_EQUAL, true));
            newQuery.addCriteria(new NeutralCriteria("authorized_for_all_edorgs", NeutralCriteria.OPERATOR_EQUAL, false));

            ids = repo.findAllIds("application", newQuery);
            for (String id : ids) {
                idList.add(id);
            }
            query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, idList));
        } //else - operator -- sees all apps
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
    public Response getApplication(@PathParam(UUID) String uuid, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        Response resp = super.read(uuid, headers, uriInfo);
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
                //only see client id and secret if you're an app developer and it's approved
                if (SecurityUtil.hasRight(Right.DEV_APP_CRUD)) {
                    if (!reg.get("status").equals("APPROVED")) {
                        appMap.remove(CLIENT_ID);
                        appMap.remove(CLIENT_SECRET);
                    }
                } else if (!SecurityUtil.hasRight(Right.SLC_APP_APPROVE)) {  //or if your an operator
                    appMap.remove(CLIENT_ID);
                    appMap.remove(CLIENT_SECRET);
                }
            }
        }
    }

    @DELETE
    @Path("{" + UUID + "}")
    public Response deleteApplication(@PathParam(UUID) String uuid, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.DEV_APP_CRUD)) {
            EntityBody body = new EntityBody();
            body.put("message", "You cannot delete this application");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }
        
        EntityBody ent = service.get(uuid);
        if (ent != null) {
            validateDeveloperHasAccessToApp(ent);
        } //if it is null, then we'll let the super.delete handle the case of deleting an app with bad ID
        return super.delete(uuid, headers, uriInfo);
    }

    // TODO app creation and app approval should be broken into separate endpoints.
    @SuppressWarnings("unchecked")
    @PUT
    @Path("{" + UUID + "}")
    public Response updateApplication(@PathParam(UUID) String uuid, EntityBody app, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        if (!missingRequiredUrls(app)) {
            EntityBody body = new EntityBody();
            body.put("message", "Applications that are not marked as installed must have a application url and redirect url");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        EntityBody oldApp = service.get(uuid);

        // The client id and secret could be null if they were filtered from the client
        String clientSecret = (String) app.get(CLIENT_SECRET);
        if (clientSecret == null) {
            app.put(CLIENT_SECRET, oldApp.get(CLIENT_SECRET));
        }
        String clientId = (String) app.get(CLIENT_ID);
        if (clientId == null) {
            app.put(CLIENT_ID, oldApp.get(CLIENT_ID));
        }
        
        String createdBy = (String) app.get(CREATED_BY);
        if (createdBy == null) {
            app.put(CREATED_BY, oldApp.get(CREATED_BY));
        }

        String id = (String) app.get("id");
        Map<String, Object> oldReg = (Map<String, Object>) oldApp.get(REGISTRATION);
        Map<String, Object> newReg = (Map<String, Object>) app.get(REGISTRATION);
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
                || (createdBy != null && !createdBy.equals(oldApp.get(CREATED_BY)))
                || (!registrationDatesMatch(oldReg, newReg, APPROVAL_DATE))
                || (!registrationDatesMatch(oldReg, newReg, REQUEST_DATE))) {

            EntityBody body = new EntityBody();
            body.put("message",
                    "Cannot modify attribute (id|client_secret|client_id|request_date|approval_date|created_by) specified in PUT.  "
                            + "Remove attribute and try again.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        changedKeys.remove("registration");
        changedKeys.remove("metaData");

        // Operator - can only change registration status
        if (SecurityUtil.hasRight(Right.SLC_APP_APPROVE)) {
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

        } else if (SecurityUtil.hasRight(Right.DEV_APP_CRUD)) {  // App Developer
            validateDeveloperHasAccessToApp(oldApp);
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

            // when a denied or unreg'ed app is altered, it goes back into pending
            if (oldRegStatus.equals("DENIED") || oldRegStatus.equals("UNREGISTERED")) {

                newReg.put(STATUS, "PENDING");
                newReg.put(REQUEST_DATE, System.currentTimeMillis());
            }


            if (sandboxEnabled && app.containsKey(AUTHORIZED_ED_ORGS)) {
                // Auto-approve whatever districts are selected.
                List<String> edOrgs = (List) app.get(AUTHORIZED_ED_ORGS);

                //validate sandbox user isn't trying to authorize an edorg outside of their tenant
                if (!edOrgsBelongToTenant(edOrgs)) {
                    EntityBody body = new EntityBody();
                    body.put("message", "Attempt to authorized edorg in sandbox outside of tenant.");
                    return Response.status(Status.BAD_REQUEST).entity(body).build();
                }

                service = store.lookupByResourceName(ApplicationAuthorizationResource.RESOURCE_NAME).getService();
                iterateEdOrgs(uuid, edOrgs);
            }
        } else {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to update application.");
            return Response.status(Status.BAD_REQUEST).entity(body).build();
        }

        //Make sure bootstrap and auto_authorize/approve fields aren't ever modified
        for (String fieldName : PERMANENT_FIELDS) {
            if (!oldApp.containsKey(fieldName)) {
                app.remove(fieldName);
            }
        }


        return super.update(uuid, app, headers, uriInfo);
    }

    private void validateDeveloperHasAccessToApp(EntityBody app) {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getExternalId().equals(app.get(CREATED_BY))) {
            throw new AccessDeniedException("Developer " + principal.getExternalId() 
                    + " is not the creator of this app and cannot modify it.");
        }
    }

    /**
     * In sandbox mode, a user can only authorize edorgs stamped with the user's own tenant ID,
     * which is the external ID (email address) of the user.
     *
     * @param edOrgs
     * @return
     */
    private boolean edOrgsBelongToTenant(List<String> edOrgs) {
        SecurityContext context = SecurityContextHolder.getContext();
        SLIPrincipal principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
        String sandboxTenant = principal.getExternalId();
        EntityService edorgService = store.lookupByResourceName(ResourceNames.EDUCATION_ORGANIZATIONS).getService();


        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, edOrgs, false));
        return edOrgs.size() == edorgService.count(query);
    }

    private void iterateEdOrgs(String uuid, List<String> edOrgIds) {
        for (String edOrgId : edOrgIds) {
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("authId", NeutralCriteria.OPERATOR_EQUAL, edOrgId));
            Iterable<EntityBody> auths = service.list(query);
            long count = service.count(query);
            if (count == 0) {
                debug("No application authorization exists. Creating one.");
                EntityBody body = new EntityBody();
                body.put("authType", "EDUCATION_ORGANIZATION");
                body.put("authId", edOrgId);
                body.put("appIds", new ArrayList());
                service.create(body);
                auths = service.list(query);
            }
            updateAuthorization(uuid, auths);
        }
    }

    private void updateAuthorization(String uuid, Iterable<EntityBody> auths) {
        for (EntityBody auth : auths) {
            List<String> appsIds = (List) auth.get("appIds");
            appsIds.add(uuid);
            auth.put("appIds", appsIds);
            service.update((String) auth.get("id"), auth);
        }
    }

    /**
     * Compares two date fields on the registration object.
     *
     * It's only tricky because one or both could be null
     * e.g. an app in pending state won't have an approval date field
     *
     * @param oldReg
     * @param newReg
     * @param field
     *            either request_date or approval_date
     * @return
     */
    private boolean registrationDatesMatch(Map<String, Object> oldReg, Map<String, Object> newReg, String field) {

        Long oldDate = (Long) oldReg.get(field);
        Long newDate = (Long) newReg.get(field);

        if (oldDate == newDate) {
            return true;
        } else if (oldDate != null && newDate != null) {
            return oldDate.equals(newDate);
        }
        return false;
    }

    private boolean missingRequiredUrls(EntityBody body) {
        String redirectUrl = (String) body.get("redirect_uri");
        String applicationUrl = (String) body.get("application_url");

        if (!(Boolean) body.get("installed")) {
            if (redirectUrl == null || redirectUrl.isEmpty() || applicationUrl == null || applicationUrl.isEmpty()) {
                return false;
            }
        } else {
            if ((redirectUrl != null && redirectUrl.length() > 0) || (applicationUrl != null && applicationUrl.length() > 0)) {
                return false;
            }
        }

        return true;
    }


}
