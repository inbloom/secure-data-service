package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.client.constants.EntityNames;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * TeacherEdOrgContextResolver
 * Determine security authorization.
 * Finds the education organization that an educator user has context to access.
 */
@Component
public class TeacherEdOrgContextResolver implements EntityContextResolver {

    @Autowired
    private Repository<Entity> repository;
    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public List<String> findAccessible(Entity principal) {
        // find schools the teacher can reach
        List<String> schoolIds = helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS));

        // find edOrgs directly referenced by schools
        List<String> edorgIds = new ArrayList<String>();
        for (String schoolId : schoolIds) {
            Entity school = this.repository.findById(EntityNames.EDUCATION_ORGANIZATION, schoolId);
            if (school.getBody().get("parentEducationAgencyReference") != null) {
                edorgIds.add(school.getBody().get("parentEducationAgencyReference").toString());
            }
        }

        //Dig up the tree of ed orgs
        Set<String> finalEdorgIds = new HashSet<String>(edorgIds);
        while (edorgIds.size() > 0) {
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("_id", "in", edorgIds, false));
            Iterable<Entity> ids = repository.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
            edorgIds.clear();
            for (Entity e : ids) {
                if (e.getBody().get("parentEducationAgencyReference") != null) {
                    edorgIds.add(e.getBody().get("parentEducationAgencyReference").toString());
                }
                finalEdorgIds.add(e.getEntityId());
            }
        }

        return new ArrayList<String>(finalEdorgIds);
    }

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.EDUCATION_ORGANIZATION.equals(toEntityType);
    }
}
