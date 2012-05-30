package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves which assessment a given staff is allowed to see
 * 
 * @author asaarela
 * 
 */
@Component
public class StaffAssessmentResolver implements EntityContextResolver {
    
    @Autowired
    private Repository<Entity> repository;
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.STAFF.equals(fromEntityType) && EntityNames.ASSESSMENT.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        
        // TODO need to figure out business logic to determine which assessment is allow for
        // given staff, allow access to all assessments temporarily
        List<String> ids = new ArrayList<String>();
        Iterable<String> it = repository.findAllIds(EntityNames.ASSESSMENT, null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
}
