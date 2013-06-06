package org.slc.sli.api.security.context.validator;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class StudentToSubStudentValidator extends AbstractContextValidator {

    protected static final Set<String> SUB_STUDENT_ENTITIES = new HashSet<String>(Arrays.asList(
            EntityNames.ATTENDANCE,
            EntityNames.STUDENT_ACADEMIC_RECORD,
            EntityNames.STUDENT_ASSESSMENT,
            EntityNames.STUDENT_GRADEBOOK_ENTRY,
            EntityNames.GRADE,
            EntityNames.REPORT_CARD));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return SUB_STUDENT_ENTITIES.contains(entityType) && isStudent();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(SUB_ENTITIES_OF_STUDENT, entityType, ids)) {
            return false;
        }

        // Get the Student IDs on the things we want to see, compare with the IDs of yourself
        Set<String> studentIds = new HashSet<String>(getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(ids), ParameterConstants.STUDENT_ID));

        return getDirectStudentIds().containsAll(studentIds);
    }
}
