package org.slc.sli.api.security.oauth;

import java.util.List;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAuthorizationValidator.class);

    @Autowired
    private Repository<Entity> repo;

    @Autowired
    private ContextResolverStore contextResolverStore;

    /**
     * Get the list of authorized apps for the user.
     * @param principal
     * 
     * @return either the list of app IDs or null if no ed-org information can be determined
     */
    public List<String> getAuthorizedApps(SLIPrincipal principal) {
        Entity district = findUsersDistrict(principal);
        if (district != null) {
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("authId", "=", district.getEntityId()));
            query.addCriteria(new NeutralCriteria("authType", "=", "EDUCATION_ORGANIZATION"));
            Entity authorizedApps = repo.findOne("applicationAuthorization", query);
            if (authorizedApps != null) {
                return (List<String>) authorizedApps.getBody().get("appIds");
            }
        }
        return null;
    }

    private Entity findUsersDistrict(SLIPrincipal principal) {

        if (principal.getEntity() != null) {
            try {
                List<String> edOrgs = contextResolverStore.findResolver(EntityNames.TEACHER, EntityNames.EDUCATION_ORGANIZATION).findAccessible(principal.getEntity());
                for (String id : edOrgs) {
                    Entity entity = repo.findById(EntityNames.EDUCATION_ORGANIZATION, id);
                    List<String> category = (List<String>) entity.getBody().get("organizationCategories");
                    if (category.contains("Local Education Agency")) {
                        return entity;
                    }
                }
            } catch (IllegalArgumentException ex) {
                //this is what the resolver throws if it doesn't find any edorg data
                LOGGER.warn("Could not find an associated ed-org for {}.", principal.getExternalId());
            }
        } else {
            LOGGER.warn("Skipping LEA lookup for {} because no entity data was found.", principal.getExternalId());
        }
        LOGGER.warn("Could not find an associated LEA for {}.", principal.getExternalId());
        return null;
    }

}
