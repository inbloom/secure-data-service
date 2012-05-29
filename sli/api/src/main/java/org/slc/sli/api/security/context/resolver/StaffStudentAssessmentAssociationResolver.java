package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves which student assessments a given staff is allowed to see
 * 
 * @author asaarela
 * 
 */
@Component
public class StaffStudentAssessmentAssociationResolver implements EntityContextResolver {
    
    @Autowired
    private Repository<Entity> repository;
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.STAFF.equals(fromEntityType)
                && EntityNames.STUDENT_ASSESSMENT_ASSOCIATION.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        
        List<String> ids = new ArrayList<String>();
        Iterable<String> it = repository.findAllIds(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION, null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
}
