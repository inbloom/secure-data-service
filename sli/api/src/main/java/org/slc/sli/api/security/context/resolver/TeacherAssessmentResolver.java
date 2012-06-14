package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Teachers should be able to see all assessments, regardless of context.
 *
 */
@Component
public class TeacherAssessmentResolver implements EntityContextResolver {

    @Autowired
    private Repository<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.ASSESSMENT.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> ids = new ArrayList<String>();
        Iterable<String> it = this.repository.findAllIds(EntityNames.ASSESSMENT, null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
}
