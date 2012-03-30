package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.ApplicationAuthorizationValidator;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(ApprovedApplicationResource.class);

    private static final String[] ALLOWED_ATTRIBUTES = new String[] {
        "application_url", "administration_url", "image_url", "description", 
        "name", "developer_info", "version", "is_admin", "behavior"
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

    private List<String> getAllowedApps() {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
        }
        if (principal == null) {
            throw new InsufficientAuthenticationException("Application list is a protected resource.");
        }
        return appValidator.getAuthorizedApps(principal);
    }

    private boolean isUserAnAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication().getAuthorities().contains(Right.ADMIN_ACCESS);
    }

    @GET
    public Response getApplications() {
        List<String> allowedApps = getAllowedApps();

        //For now, null (meaning no LEA data for the user) defaults to all apps
        if (allowedApps == null) {
            allowedApps = new ArrayList<String>();

            //the app list is an admin-protected resource, so we must sudo it
            Iterable<String> appIds = SecurityUtil.sudoRun(new SecurityTask<Iterable<String>>()  {
                @Override
                public  Iterable<String> execute() {
                    return service.listIds(new NeutralQuery());
                }
            });

            for (String id : appIds) {
                allowedApps.add(id);
            }
        }

        List<EntityBody> results = new ArrayList<EntityBody>();

        boolean isAdmin = isUserAnAdmin();
        LOG.debug("User is an administrator? {}", isAdmin);

        for (final String id : allowedApps) {

            EntityBody result  = SecurityUtil.sudoRun(new SecurityTask<EntityBody>()  {
                @Override
                public  EntityBody execute() {
                    return service.get(id);
                }
            });

            if (result != null) {

                //don't allow non-admins to see admin apps
                if ((Boolean) result.get("is_admin") && !isAdmin) {
                    continue;
                }

                //don't allow disabled apps
                if (!(Boolean) result.get("enabled")) {
                    continue;
                }

                filterAttributes(result);
                results.add(result);
            }
        }
        return Response.status(Status.OK).entity(results).build();
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
