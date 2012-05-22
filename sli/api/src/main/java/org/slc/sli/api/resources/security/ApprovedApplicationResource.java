package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.ApplicationAuthorizationValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Used to retrieve the list of apps that a user is allowed to use.
 * 
 * @author pwolf
 *
 */
@Component
@Scope("request")
@Path("/userapps")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class ApprovedApplicationResource {

    public static final String RESOURCE_NAME = "application"; 

    private static final String[] ALLOWED_ATTRIBUTES = new String[] {
        "application_url", "administration_url", "image_url", "description", 
 "name", "vendor", "version", "is_admin", "behavior", "endpoints"
    };

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private ApplicationAuthorizationValidator appValidator;

    private EntityService service;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(RESOURCE_NAME);
        this.service = def.getService();
    }

    @SuppressWarnings("unchecked")
    @GET
    public Response getApplications(@DefaultValue("") @QueryParam("is_admin") String adminFilter) {
        List<String> allowedApps = getAllowedAppIds();

        List<EntityBody> results = new ArrayList<EntityBody>();

        for (final String id : allowedApps) {

            EntityBody result  = SecurityUtil.sudoRun(new SecurityTask<EntityBody>()  {
                @Override
                public  EntityBody execute() {
                    try {
                        return service.get(id);
                    } catch (EntityNotFoundException e) {
                        //Probably means that the application is in the appAuthorization 
                        //collection but not the application one.
                        return null;
                    }
                }
            });


            if (result != null) {
                
                if (!shouldFilterApp(result, adminFilter)) {

                    filterAttributes(result);
                    results.add(result);
                }
            }
        }
        return Response.status(Status.OK).entity(results).build();
    }
    
    private boolean shouldFilterApp(EntityBody result, String adminFilter) {
        if (result.containsKey("endpoints")) {
            List<Map<String, Object>> endpoints = (List<Map<String, Object>>) result.get("endpoints");
            filterEndpoints(endpoints);
            
            //we ended up filtering out all the endpoints - no reason to display the app
            if (endpoints.size() == 0) {
                return true;
            }
        }
        
        boolean isAdminApp = result.containsKey("is_admin") ? Boolean.valueOf((Boolean) result.get("is_admin")) : false;

        //is_admin query param specified
        if (!adminFilter.equals("")) {
            boolean adminFilterVal = Boolean.valueOf(adminFilter);

            //non-admin app, but is_admin == true
            if (!isAdminApp && adminFilterVal) {
                return true;
            }

            //admin app, but is_admin == false
            if (isAdminApp && !adminFilterVal) {
                return true;
            }
        }

        // don't allow "installed" apps
        if (result.get("installed") == null || (Boolean) result.get("installed")) {
            return true;
        }
        
        //make sure hosted SLI users can only see admin and portal
        if (isHostedUser()) {
            String name = (String) result.get("name");
            String dev = (String) result.get("created_by");
            if (dev != null && dev.equals("slcdeveloper")) {
                if (!name.startsWith("Admin") && !name.startsWith("Portal")) {
                    //somewhat quick and dirty way of checking for admin/portal
                    //maybe we should add special flag to the app instead
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Host users are those who are hosted in the SLI's IDP.
     * 
     * They only have access to admin tools and portal.
     * 
     * @return
     */
    private boolean isHostedUser() {
        List<String> userRoles = getUsersRoles();
        return userRoles.contains(RoleInitializer.APP_DEVELOPER)
                || userRoles.contains(RoleInitializer.LEA_ADMINISTRATOR)
                || userRoles.contains(RoleInitializer.SEA_ADMINISTRATOR)
                || userRoles.contains(RoleInitializer.SLC_OPERATOR)
                || userRoles.contains(RoleInitializer.REALM_ADMINISTRATOR);
        
    }
    
    private List<String> getUsersRoles() {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> userRoles = principal.getRoles();
        return userRoles;
    }

    private void filterEndpoints(List<Map<String, Object>> endpoints) {
        List<String> userRoles = getUsersRoles();
        for (Iterator<Map<String, Object>> i = endpoints.iterator(); i.hasNext();) {

            @SuppressWarnings("unchecked")
            List<String> reqRoles = (List<String>) i.next().get("roles");
            
            //if no roles specified, don't filter it
            if (reqRoles.size() == 0) {
                continue;
            }
            
            List<String> intersection = new ArrayList<String>(reqRoles);
            intersection.retainAll(userRoles);
            if (userRoles.size() == 0 || intersection.size() == 0) {
                debug("Removing endpoint because users roles {} did not match required roles {}.", userRoles, reqRoles);
                i.remove();
            }
        }
    }

    private List<String> getAllowedAppIds() {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
        }
        if (principal == null) {
            throw new InsufficientAuthenticationException("Application list is a protected resource.");
        }
        List<String> toReturn = appValidator.getAuthorizedApps(principal);

        //For now, null (meaning no LEA data for the user) defaults to all apps
        if (toReturn == null) {
            toReturn = new ArrayList<String>();

            //the app list is an admin-protected resource, so we must sudo it
            Iterable<String> appIds = SecurityUtil.sudoRun(new SecurityTask<Iterable<String>>()  {
                @Override
                public  Iterable<String> execute() {
                    return service.listIds(new NeutralQuery());
                }
            });

            for (String id : appIds) {
                toReturn.add(id);
            }
        }
        return toReturn;
    }

    /**
     * Filters out attributes we don't want ordinary users to see.
     * For example, they should never see the client_secret.
     * 
     * @param result
     */
    private void filterAttributes(EntityBody result) {
        for (Iterator<Map.Entry<String, Object>> i = result.entrySet().iterator(); i.hasNext();) {
            String key = i.next().getKey();
            if (!Arrays.asList(ALLOWED_ATTRIBUTES).contains(key)) {
                i.remove();
            }
        }

    }
}
