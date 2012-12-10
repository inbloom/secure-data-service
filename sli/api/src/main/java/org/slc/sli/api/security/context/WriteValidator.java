package org.slc.sli.api.security.context;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.HashMap;

/**
 * Verify the user has access to write to an entity. We do this by determining the entities edOrg and the principals edOrgs
 * Verify the user has access to the entity once their changes are applied.
 */
@Component
public class WriteValidator {

    private HashMap<String, String> ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION;

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @PostConstruct
    private void init() {
        ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION = new HashMap<String, String>() {{
            put(EntityNames.ATTENDANCE, "schoolId");
            put(EntityNames.COHORT, "educationOrgId");
            put(EntityNames.COURSE, "schoolId");
            put(EntityNames.COURSE_OFFERING, "schoolId");
            put(EntityNames.DISCIPLINE_INCIDENT, "schoolId");
            put(EntityNames.GRADUATION_PLAN, "educationOrganizationId");
            put(EntityNames.SECTION, ParameterConstants.SCHOOL_ID);
            put(EntityNames.SESSION, "schoolId");
            put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, "educationOrganizationId");
            put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "schoolId");
        }};
    }


    public void validateWriteRequest(EntityBody entityBody, UriInfo uriInfo, SLIPrincipal principal) {

        if (!isValidForEdOrgWrite(entityBody, uriInfo, principal)) {
            throw new AccessDeniedException("Trying to write an entity outside of your education organization hierarchy");
        }

    }

    private boolean isValidForEdOrgWrite(EntityBody entityBody, UriInfo uriInfo, SLIPrincipal principal) {
        boolean isValid = true;

        int RESOURCE_SEGMENT_INDEX = 1;
        if (uriInfo.getPathSegments().size() > RESOURCE_SEGMENT_INDEX) {

            String resourceName = uriInfo.getPathSegments().get(RESOURCE_SEGMENT_INDEX).getPath();
            EntityDefinition def = store.lookupByResourceName(resourceName);

            int IDS_SEGMENT_INDEX = 2;
            if (uriInfo.getPathSegments().size() > IDS_SEGMENT_INDEX) {
                // look if we have ed org write context to already existing entity
                String id = uriInfo.getPathSegments().get(IDS_SEGMENT_INDEX).getPath();
                Entity existingEntity = repo.findById(store.lookupByResourceName(resourceName).getStoredCollectionName(), id);
                isValid = isEntityValidForEdOrgWrite(existingEntity, principal);
            }

            if (entityBody != null && !entityBody.isEmpty()) {
                if (ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(def.getType()) != null) {
                    String edOrgId = (String) entityBody.get(ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(def.getType()));
                    isValid = principal.getSubEdOrgHierarchy().contains(edOrgId);
                }
            }
        }
        return isValid;
    }


    private boolean isEntityValidForEdOrgWrite(Entity entity, SLIPrincipal principal) {
        if (entity == null) {
            return false;
        }
        boolean isValid = true;
        if (ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(entity.getType()) != null) {
            Collection<String> principalsEdOrgs = principal.getSubEdOrgHierarchy();
            String edOrgId = (String) entity.getBody().get(ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(entity.getType()));
            isValid = principalsEdOrgs.contains(edOrgId);
        }
        return isValid;
    }

    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
