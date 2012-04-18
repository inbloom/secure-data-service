package org.slc.sli.api.security.context.resolver;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.domain.Entity;

/**
 * Resolves which DisciplineIncident a given teacher is allowed to see
 *
 * @author syau
 *
 */
@Component
public class TeacherDisciplineIncidentResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.DISCIPLINE_INCIDENT.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        return helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SECTION_ASSOCIATIONS,
                                                              ResourceNames.STUDENT_SECTION_ASSOCIATIONS,
                                                              ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS));
    }
}
