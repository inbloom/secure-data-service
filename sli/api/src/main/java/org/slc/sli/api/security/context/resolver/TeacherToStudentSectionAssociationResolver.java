package org.slc.sli.api.security.context.resolver;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
//import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;


//import org.slc.sli.api.client.constants.v1.ParameterConstants;

/**
 * Resolves which StudentSectionAssociation a given teacher is allowed to see.
 *
 */
@Component
public class TeacherToStudentSectionAssociationResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentSectionAssociationEndDateFilter nodeDateFilter;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT_SECTION_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS, ResourceNames.STUDENT_SECTION_ASSOCIATIONS));

        return helper.findEntitiesContainingReference(EntityNames.STUDENT_SECTION_ASSOCIATION, "studentId", studentIds,
                Arrays.asList((NodeFilter) nodeDateFilter));
    }
}
