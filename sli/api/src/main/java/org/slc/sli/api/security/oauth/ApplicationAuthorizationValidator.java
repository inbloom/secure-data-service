package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Determines which applications a given user is authorized to use based on
 * that user's ed-org.
 * 
 * @author pwolf
 * 
 */
@Component
public class ApplicationAuthorizationValidator {
    
    @Autowired
    private Repository<Entity> repo;
    
    @Autowired
    private ContextResolverStore contextResolverStore;
    
    @Autowired
    @Value("${sli.sandbox.autoRegisterApps}")
    private boolean autoRegister;
    
    /**
     * Get the list of authorized apps for the user based on the user's LEA.
     * 
     * No additional filtering is done on the results. E.g. if a user is a non-admin,
     * the admin apps will still show up in the list, or if an app is disabled it will
     * still show up.
     * 
     * @param principal
     * 
     * @return list of app IDs, or null if it couldn't be determined
     */
    @SuppressWarnings("unchecked")
    public List<String> getAuthorizedApps(SLIPrincipal principal) {
        List<Entity> districts = findUsersDistricts(principal);
        Set<String> results = getDefaultAuthorizedApps();
        
        // essentially allow by default for users with no entity data, ie. administrators
        if (districts == null) {
            return new ArrayList<String>(results);
        }
        
        for (Entity district : districts) {
            debug("User is in district " + district.getEntityId());
            
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("authId", "=", district.getBody().get("stateOrganizationId")));
            query.addCriteria(new NeutralCriteria("authType", "=", "EDUCATION_ORGANIZATION"));
            Entity authorizedApps = repo.findOne("applicationAuthorization", query);
            
            if (authorizedApps != null) {
                
                NeutralQuery districtQuery = new NeutralQuery();
                districtQuery.addCriteria(new NeutralCriteria("authorized_ed_orgs", "=", district.getBody().get(
                        "stateOrganizationId")));
                
                Set<String> vendorAppsEnabledForEdorg = new HashSet<String>();
                
                for (String id : repo.findAllIds("application", districtQuery)) {
                    vendorAppsEnabledForEdorg.add(id);
                }
                
                //Intersection
                vendorAppsEnabledForEdorg.retainAll((List<String>) authorizedApps.getBody().get("appIds"));
                
                results.addAll(vendorAppsEnabledForEdorg);
            }
        }
 
        return new ArrayList<String>(results);
    }
    
    /**
     * 
     * @return
     */
    private Set<String> getDefaultAuthorizedApps() {
        Set<String> toReturn = new HashSet<String>();
        NeutralQuery bootstrapQuery = new NeutralQuery();
        bootstrapQuery.addCriteria(new NeutralCriteria("bootstrap", "=", true));
        Iterable<Entity> bootstrapApps = repo.findAll("application", bootstrapQuery);
        
        for (Entity currentApp : bootstrapApps) {
            if (isSandbox()) {
                toReturn.add(currentApp.getEntityId());
            } else {
                String appName = (String) currentApp.getBody().get("name");
                if (appName.indexOf("Admin") > -1 || appName.indexOf("Portal") > -1) {
                    toReturn.add(currentApp.getEntityId()); 
                }
            }
        }
        return toReturn;
    }
    
    
    private boolean isSandbox() {
        return autoRegister;
    }
    
    /**
     * Looks up the user's LEA entity.
     * 
     * Currently it returns a list of all LEAs the user might be associated with.
     * In the case there's a hierarchy of LEAs, all are returned in no particular order.
     * 
     * @param principal
     * @return a list of accessible LEAs, or null if no entity data was found
     */
    private List<Entity> findUsersDistricts(SLIPrincipal principal) {
        List<Entity> toReturn = new ArrayList<Entity>();
        if (principal.getEntity() != null) {
            List<String> edOrgs = null;
            try {
                edOrgs = contextResolverStore.findResolver(EntityNames.TEACHER, EntityNames.EDUCATION_ORGANIZATION)
                        .findAccessible(principal.getEntity());
            } catch (IllegalArgumentException ex) {
                // DE260 - Logging of possibly sensitive data
                // this is what the resolver throws if it doesn't find any edorg data
                // LOGGER.warn("Could not find an associated ed-org for {}.",
                // principal.getExternalId());
                warn("Could not find an associated ed-org for the given principal.");
            }
            if (edOrgs == null || edOrgs.size() == 0) {   // maybe user is a staff?
                edOrgs = contextResolverStore.findResolver(EntityNames.STAFF, EntityNames.EDUCATION_ORGANIZATION)
                        .findAccessible(principal.getEntity());
            }
            
            edOrgs.remove("-133"); //avoid querying bad mongo ID

            for (String id : edOrgs) {
                Entity entity = repo.findById(EntityNames.EDUCATION_ORGANIZATION, id);
                if (entity == null) {
                    warn("Could not find ed-org with ID {}", id);
                } else {
                    List<String> category = (List<String>) entity.getBody().get("organizationCategories");
                    if (category != null) {
                        if (category.contains("Local Education Agency")) {
                            toReturn.add(entity);
                        }
                    }
                    
                }
            }
            
        } else {
            // DE260 - Logging of possibly sensitive data
            // LOGGER.warn("Skipping LEA lookup for {} because no entity data was found.",
            // principal.getExternalId());
            return null;
        }
        if (toReturn.size() == 0) {
            // DE260 - Logging of possibly sensitive data
            // LOGGER.warn("Could not find an associated LEA for {}.", principal.getExternalId());
            warn("Could not find an associated LEA for the given principal");
        }
        return toReturn;
    }

    
}
