package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Scope("request")
@Path("/applicationAuthorization")
@Produces({ Resource.JSON_MEDIA_TYPE+";charset=utf-8" })
public class ApplicationAuthorizationResource {
    
    @Autowired
    private EntityDefinitionStore store;
    
    @Autowired
    Repository<Entity> repo;
    
    @Autowired
    private SecurityEventBuilder securityEventBuilder;
    
    @Autowired
    private DelegationUtil delegationUtil;
    
    private EntityService service;
    private EntityService applicationService;
    private EntityService edOrgService;
    
    public static final String RESOURCE_NAME = "applicationAuthorization";
    
    public static final String UUID = "uuid";
    public static final String AUTH_ID = "authId";
    public static final String AUTH_TYPE = "authType";
    public static final String EDORG_AUTH_TYPE = "EDUCATION_ORGANIZATION";
    public static final String APP_IDS = "appIds";
    public static final String STATE_ORGANIZATION_ID = "stateOrganizationId";
    public static final String NAME_OF_INSTITUTION = "nameOfInstitution";
    public static final String CLIENT_ID = "clientId";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    
    public static final String RESOURCE_APPLICATION = "application";
    public static final String RESOURCE_EDORG = "educationOrganization";
    
    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(RESOURCE_NAME);
        service = def.getService();
        
        EntityDefinition appDef = store.lookupByResourceName(RESOURCE_APPLICATION);
        applicationService = appDef.getService();
        
        EntityDefinition edOrgDef = store.lookupByResourceName(ResourceNames.EDUCATION_ORGANIZATIONS);
        edOrgService = edOrgDef.getService();
    }
    
    @GET
    @Path("{" + UUID + "}")
    public Response getAuthorization(@PathParam(UUID) String uuid) {
        
        if (uuid != null) {
            EntityBody entityBody = service.get(uuid);
            if (entityBody != null) {
                verifyAccess((String) entityBody.get(AUTH_ID), null);
                return Response.status(Status.OK).entity(entityBody).build();
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }
    
    @POST
    public Response createAuthorization(EntityBody newAppAuth, @Context final UriInfo uriInfo) {
        SecurityUtil.ensureAuthenticated();
        if (!SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ) && !SecurityUtil.hasRight(Right.EDORG_DELEGATE)) {
            return SecurityUtil.forbiddenResponse();
        }
        
        verifyAccess((String) newAppAuth.get(AUTH_ID), null);
        
        String uuid = service.create(newAppAuth);
        logChanges(uriInfo, null, newAppAuth);
        String uri = uriToString(uriInfo) + "/" + uuid;
        return Response.status(Status.CREATED).header("Location", uri).build();
    }
    
    @PUT
    @Path("{" + UUID + "}")
    public Response updateAuthorization(@PathParam(UUID) String uuid, EntityBody auth, @Context final UriInfo uriInfo) {
        SecurityUtil.ensureAuthenticated();
        if (!SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ) && !SecurityUtil.hasRight(Right.EDORG_DELEGATE)) {
            return SecurityUtil.forbiddenResponse();
        }
        
        EntityBody oldAuth = service.get(uuid);
        String oldTenant = (String) ((Map<String, Object>) oldAuth.get("metaData")).get("tenantId");
        verifyAccess((String) oldAuth.get(AUTH_ID), oldTenant);
        
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
        if (status) { // if the entity was changed
            logChanges(uriInfo, oldAuth, auth);
        }
        if (status) {
            return Response.status(Status.NO_CONTENT).build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }
    
    @GET
    public Response getAuthorizations(@Context UriInfo info) {
        SecurityUtil.ensureAuthenticated();
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        String edOrg = delegationUtil.getUsersStateUniqueId();
        
        if (!SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ) && !SecurityUtil.hasRight(Right.EDORG_DELEGATE)) {
            return SecurityUtil.forbiddenResponse();
        }
        
        if (SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ)) {
            if (edOrg != null) {
                NeutralQuery query = new NeutralQuery();
                query.addCriteria(new NeutralCriteria(AUTH_TYPE, "=", EDORG_AUTH_TYPE));
                query.addCriteria(new NeutralCriteria(AUTH_ID, "=", edOrg));
                query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", SecurityUtil.getTenantId(), false));
                Entity ent = repo.findOne(RESOURCE_NAME, query);
                if (ent != null) {
                    ent.getBody().put("link", uriToString(info) + "/" + ent.getEntityId());
                    ent.getBody().put("id", ent.getEntityId());
                    results.add(ent.getBody());
                }
            }
        } else if (SecurityUtil.hasRight(Right.EDORG_DELEGATE)) {
            List<String> delegateEdOrgs = delegationUtil.getDelegateEdOrgs();
            for (String curEdOrg : delegateEdOrgs) {
                NeutralQuery finalQuery = new NeutralQuery();
                finalQuery.addCriteria(new NeutralCriteria(AUTH_TYPE, "=", EDORG_AUTH_TYPE));
                finalQuery.addCriteria(new NeutralCriteria(AUTH_ID, "=", curEdOrg));
                finalQuery.addCriteria(new NeutralCriteria("metaData.tenantId", "=", SecurityUtil.getTenantId()));
                Entity ent = repo.findOne(RESOURCE_NAME, finalQuery);
                if (ent != null) {
                    ent.getBody().put("link", uriToString(info) + "/" + ent.getEntityId());
                    ent.getBody().put("id", ent.getEntityId());
                    results.add(ent.getBody());
                }
            }
            
        }
        return Response.status(Status.OK).entity(results).build();
    }
    
    private static String uriToString(UriInfo uri) {
        return uri.getBaseUri() + uri.getPath().replaceAll("/$", "");
    }
    
    private void verifyAccess(String authId, String tenantId) throws AccessDeniedException {
        String edOrg = delegationUtil.getUsersStateUniqueId();
        if (edOrg == null) {
            throw new InsufficientAuthenticationException("No edorg exists on principal.");
        }
        
        String usersTenant = SecurityUtil.getTenantId();
        if (tenantId != null && !tenantId.equals(usersTenant)) {
            throw new AccessDeniedException("User cannot modify application authorizations outside of their tenant");
        }
        
        List<String> delegateEdOrgs = delegationUtil.getDelegateEdOrgs();
        if (!edOrg.equals(authId) && !delegateEdOrgs.contains(authId)) {
            throw new AccessDeniedException("User can only access " + edOrg);
        }
    }
    
    private void logChanges(UriInfo uriInfo, EntityBody oldAppAuth, EntityBody newAppAuth) {
        String oldEdOrgId = "";
        String newEdOrgId = "";
        if (oldAppAuth != null && oldAppAuth.get(AUTH_ID) != null) {
            oldEdOrgId = (String) oldAppAuth.get(AUTH_ID);
        }
        if (newAppAuth != null && newAppAuth.get(AUTH_ID) != null) {
            newEdOrgId = (String) newAppAuth.get(AUTH_ID);
        }
        
        List<Object> oldApprovedAppIds = new ArrayList<Object>();
        List<Object> newApprovedAppIds = new ArrayList<Object>();
        if (oldAppAuth != null && oldAppAuth.get(APP_IDS) != null) {
            oldApprovedAppIds = (List<Object>) oldAppAuth.get(APP_IDS);
        }
        if (newAppAuth != null && newAppAuth.get(APP_IDS) != null) {
            newApprovedAppIds = (List<Object>) newAppAuth.get(APP_IDS);
        }
        
        Set<Pair<String, String>> older = new HashSet<Pair<String, String>>();
        for (Object appId : oldApprovedAppIds) {
            older.add(Pair.of(oldEdOrgId, (String) appId));
        }
        
        Set<Pair<String, String>> newer = new HashSet<Pair<String, String>>();
        for (Object appId : newApprovedAppIds) {
            newer.add(Pair.of(newEdOrgId, (String) appId));
        }
        
        Set<Pair<String, String>> added = new HashSet<Pair<String, String>>(newer);
        added.removeAll(older);
        Set<Pair<String, String>> deleted = new HashSet<Pair<String, String>>(older);
        deleted.removeAll(newer);
        
        logSecurityEvent(uriInfo, added, true);
        logSecurityEvent(uriInfo, deleted, false);
    }
    
    private void logSecurityEvent(UriInfo uriInfo, Set<Pair<String, String>> edOrgApps, boolean added) {
        for (Pair<String, String> edOrgApp : edOrgApps) {
            String stateId = edOrgApp.getLeft();
            String appId = edOrgApp.getRight();
            String edOrgId = null;
            
            EntityBody edOrg = null;
            EntityBody app = null;
            try {
                Iterable<String> edorgIds = edOrgService.listIds(new NeutralQuery(new NeutralCriteria(
                        STATE_ORGANIZATION_ID, "=", stateId)));
                if (edorgIds != null && edorgIds.iterator().hasNext()) {
                    edOrgId = edorgIds.iterator().next();
                    edOrg = edOrgService.get(edOrgId);
                }
            } catch (AccessDeniedException e) {
                info("No access to EdOrg[" + edOrgId + "].Omitting in Security ");
            }
            try {
                app = applicationService.get(appId);
            } catch (AccessDeniedException e) {
                info("No access to Application[" + appId + "].Omitting in Security ");
            } catch (EntityNotFoundException e) {
                info("Could not find application [" + appId + "]. Omitting in Security ");
            }
            String stateOrganizationId = "";
            String nameOfInstitution = "";
            if (edOrg != null) {
                if (edOrg.get(STATE_ORGANIZATION_ID) != null) {
                    stateOrganizationId = (String) edOrg.get(STATE_ORGANIZATION_ID);
                }
                if (edOrg.get(NAME_OF_INSTITUTION) != null) {
                    nameOfInstitution = (String) edOrg.get(NAME_OF_INSTITUTION);
                }
            }
            
            String clientId = null;
            String name = null;
            String description = null;
            if (app != null) {
                if (app.get(CLIENT_ID) != null) {
                    clientId = (String) app.get(CLIENT_ID);
                }
                if (app.get(NAME) != null) {
                    name = (String) app.get(NAME);
                }
                if (app.get(DESCRIPTION) != null) {
                    description = (String) app.get(DESCRIPTION);
                }
            }
            
            if (added) {
                audit(securityEventBuilder.createSecurityEvent(ApplicationAuthorizationResource.class.getName(),
                        uriInfo, "ALLOWED [" + appId + ", " + name + ", " + description + "] by Client [" + clientId
                                + "] " + "TO ACCESS [" + edOrgId + ", " + stateOrganizationId + ", "
                                + nameOfInstitution + "]"));
            } else {
                audit(securityEventBuilder.createSecurityEvent(ApplicationAuthorizationResource.class.getName(),
                        uriInfo, "NOT ALLOWED [" + appId + ", " + name + ", " + description + "] by Client ["
                                + clientId + "] " + "TO ACCESS [" + edOrgId + ", " + stateOrganizationId + ", "
                                + nameOfInstitution + "]"));
            }
        }
    }
    
}
