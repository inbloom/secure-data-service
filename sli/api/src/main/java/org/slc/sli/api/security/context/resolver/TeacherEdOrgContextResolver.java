package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TeacherEdOrgContextResolver
 * Determine security authorization.
 * Finds the education organization that an educator user has context to access.
 */
@Component
public class TeacherEdOrgContextResolver implements EntityContextResolver {

    @Autowired
    private EntityRepository repository;
    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public List<String> findAccessible(Entity principal) {
        // find schools the teacher can reach
        List<String> schoolIds = helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS));

        // find edOrgs directly referenced by schools
        List<String> edorgIds = new ArrayList<String>();
        for (String schoolId : schoolIds) {
            Entity school = this.repository.find(EntityNames.SCHOOL, schoolId);
            if (school.getBody().get("parentEducationAgencyReference") != null) {
                edorgIds.add(school.getBody().get("parentEducationAgencyReference").toString());
            }
        }

        Set<String> finalEdorgIds = new HashSet<String>(edorgIds);
        boolean added = true;
        while (added) {
            added = false;
            edorgIds = helper.findEntitiesContainingReference(EntityNames.EDUCATION_ORGANIZATION, "parentEducationAgencyReference", edorgIds);

            for (String crntEdorgId : edorgIds) {
                added |= finalEdorgIds.add(crntEdorgId);
            }
        }

        return new ArrayList<String>(finalEdorgIds);
    }

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.EDUCATION_ORGANIZATION.equals(toEntityType);
    }
}
