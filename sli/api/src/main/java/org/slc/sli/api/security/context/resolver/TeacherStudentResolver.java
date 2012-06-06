package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Resolves Teachers context to Students. Finds accessible students through section, program, and cohort associations.
 */
@Component
public class TeacherStudentResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        Set<String> ids = new TreeSet<String>();

        ids.addAll(findAccessibleThroughSection(principal));
        ids.addAll(findAccessibleThroughCohort(principal));
        ids.addAll(findAccessibleThroughProgram(principal));

        return new ArrayList<String>(ids);
    }

    private List<String> findAccessibleThroughSection(Entity principal) {
        return helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SECTION_ASSOCIATIONS,
                ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.STUDENT_PARENT_ASSOCIATIONS));
    }

    private List<String> findAccessibleThroughProgram(Entity principal) {
        return Collections.emptyList(); //TODO
    }

    private List<String> findAccessibleThroughCohort(Entity principal) {
        return Collections.emptyList(); //TODO
    }
}
