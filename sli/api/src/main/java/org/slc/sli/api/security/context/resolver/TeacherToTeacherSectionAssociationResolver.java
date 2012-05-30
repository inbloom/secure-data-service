package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;

/**
 * Resolves which TeacherSection a given teacher is allowed to see.
 */
@Component
public class TeacherToTeacherSectionAssociationResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.TEACHER_SECTION_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> ids = new ArrayList<String>(Arrays.asList(principal.getEntityId()));
        return helper.findEntitiesContainingReference(EntityNames.TEACHER_SECTION_ASSOCIATION, "teacherId", ids);
    }


}
