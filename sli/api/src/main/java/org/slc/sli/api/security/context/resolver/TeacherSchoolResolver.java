package org.slc.sli.api.security.context.resolver;

import java.util.Arrays;
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
public class TeacherSchoolResolver implements EntityContextResolver {
    
    @Autowired
    private AssociativeContextHelper helper;
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.SCHOOL.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        return helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS));
    }    
}
