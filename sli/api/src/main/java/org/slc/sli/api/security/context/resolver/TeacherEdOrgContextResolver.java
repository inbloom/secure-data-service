package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

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
        Set<String> edOrgIds = new HashSet<String>();
        for (String schoolId : schoolIds) {
            Entity school = this.repository.find(EntityNames.SCHOOL, schoolId);
            edOrgIds.add(school.getBody().get("parentEducationAgencyReference").toString());
        }
        
        // from those edOrgs, build the closure under the "parent" relationship 
        Set<String> retVal = new HashSet<String>(); 
        retVal.addAll(edOrgIds);
        while(true) {
            // in this loop, "edOrgIds" contains ids added in the previous round
            Set<String> toAdd = new HashSet<String>(); // this contains ids to be added in this round
            for (String edOrgId : edOrgIds) {
                Entity edOrg = this.repository.find(EntityNames.EDUCATION_ORGANIZATION, edOrgId);
                Object newId = edOrg.getBody().get("parentEducationAgencyReference");
                if (newId != null && !retVal.contains(newId.toString())) {
                    toAdd.add(newId.toString());
                }
            }
            if(toAdd.isEmpty()) { 
                break; // no new ids to add; closure is reached.  
            }
            edOrgIds.addAll(toAdd);
            edOrgIds = toAdd;
        }

        return new ArrayList<String> (retVal);
    }

    public void setRepository(EntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.EDUCATION_ORGANIZATION.equals(toEntityType);
    }
}
