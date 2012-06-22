package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves which learningObjective a given teacher is allowed to see
 * 
 * @author dliu
 * 
 */
@Component
public class TeacherLearningObjectiveResolver implements EntityContextResolver {

    @Autowired
    private Repository<Entity> repository;
    

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return false;
        //return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.LEARNING_OBJECTIVE.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        // TODO need to figure out business logic to determine which learning objective is allow for
        // given teacher, allow access to all learning objectives temporarily
        List<String> ids = new ArrayList<String>();
        Iterable<String> it = this.repository.findAllIds(EntityNames.LEARNING_OBJECTIVE, null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
}
