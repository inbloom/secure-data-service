package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;

/**
 * Resolves which teachers a given teacher is allowed to see
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherSessionResolver implements EntityContextResolver {
    
    @Autowired
    private AssociativeContextHelper helper;
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.SESSION.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> all = new ArrayList<String>();
        List<String> schoolBased = helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, ResourceNames.SCHOOL_SESSION_ASSOCIATIONS));
        List<String> previousSectionsForCurrentStudents = Collections.emptyList(); // FIXME add traversal to previous sections for current students
        
        all.addAll(schoolBased);
        all.addAll(previousSectionsForCurrentStudents);
        
        return all;
    }
}
