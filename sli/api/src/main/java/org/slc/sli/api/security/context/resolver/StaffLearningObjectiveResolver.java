package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Resolves which learningObjective a given staff is allowed to see
 * 
 * @author dliu
 * 
 */
@Component
public class StaffLearningObjectiveResolver implements EntityContextResolver {

    @Autowired
    private Repository<Entity> repository;
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.STAFF.equals(fromEntityType) && EntityNames.LEARNINGOBJECTIVE.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        // TODO need to figure out business logic to determine which learning objective is allow for
        // given staff, allow access to all learning objectives temporarily
        List<String> ids = new ArrayList<String>();
        Iterable<String> it = this.repository.findAllIds(EntityNames.LEARNINGOBJECTIVE, null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
}
