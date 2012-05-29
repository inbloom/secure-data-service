package org.slc.sli.api.security.context.resolver;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;

/**
 * Resolves which StudentSchoolAssociation a given teacher is allowed to see.
 */
@Component
public class TeacherToStudentSchoolAssociationResolver implements
        EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentGracePeriodNodeFilter graceFilter;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT_SCHOOL_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = graceFilter.filterIds(helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS, ResourceNames.STUDENT_SECTION_ASSOCIATIONS)));
        List<String> associationIds = helper.findEntitiesContainingReference(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "studentId", studentIds);

        debug("Accessable student-school association IDS [ {} ]", associationIds);
        return associationIds;

    }

}
