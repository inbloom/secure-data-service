package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Resolves which learning Standard a given teacher is allowed to see
 * 
 * @author dliu
 * 
 */
@Component
public class TeacherLearningStandardResolver implements EntityContextResolver {

    @Autowired
    private Repository<Entity> repository;
    

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.LEARNINGSTANDARD.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        // TODO need to figure out business logic to determine which learning standard is allow for
        // given teacher, allow access to all learning Standard temporarily
        List<String> ids = new ArrayList<String>();
        Iterable<String> it = this.repository.findAllIds(EntityNames.LEARNINGSTANDARD, null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
}
