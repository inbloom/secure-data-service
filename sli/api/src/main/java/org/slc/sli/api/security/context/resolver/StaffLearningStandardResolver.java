package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Resolves which learningStandard a given staff is allowed to see
 * 
 * @author dliu
 * 
 */
//@Component
public class StaffLearningStandardResolver implements EntityContextResolver {

    @Autowired
    private Repository<Entity> repository;
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.STAFF.equals(fromEntityType) && EntityNames.LEARNINGSTANDARD.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        // TODO need to figure out business logic to determine which learning Standard is allow for
        // given staff, allow access to all learning standards temporarily
        List<String> ids = new ArrayList<String>();
        Iterable<String> it = this.repository.findAllIds(EntityNames.LEARNINGSTANDARD, null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
}
