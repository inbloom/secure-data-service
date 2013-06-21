package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;

@Component
public class StudentDenyAllValidator extends AbstractContextValidator {

    protected static final Set<String> STUDENT_DENIED_ENTITIES = new HashSet<String>(Arrays.asList(
            EntityNames.DISCIPLINE_ACTION,
            EntityNames.DISCIPLINE_INCIDENT,
            EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,
            // to be supported in a future release, but unsupported today
            EntityNames.STAFF,
            EntityNames.STAFF_COHORT_ASSOCIATION,
            EntityNames.STAFF_ED_ORG_ASSOCIATION,
            EntityNames.STAFF_PROGRAM_ASSOCIATION,
            EntityNames.TEACHER,
            EntityNames.TEACHER_SCHOOL_ASSOCIATION,
            EntityNames.TEACHER_SECTION_ASSOCIATION ));

    protected static final Set<String> GLOBAL_ENTITIES = new HashSet<String>(Arrays.asList(
            EntityNames.PROGRAM,
            EntityNames.SECTION,
            EntityNames.LEARNING_OBJECTIVE,
            EntityNames.LEARNING_STANDARD,
            EntityNames.COURSE_OFFERING,
            EntityNames.COMPETENCY_LEVEL_DESCRIPTOR,
            EntityNames.SESSION,
            EntityNames.COURSE,
            EntityNames.STUDENT_COMPETENCY_OBJECTIVE,
            EntityNames.EDUCATION_ORGANIZATION,
            EntityNames.SCHOOL,
            EntityNames.GRADING_PERIOD,
            EntityNames.ASSESSMENT
            ));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent()
                && (STUDENT_DENIED_ENTITIES.contains(entityType) || (isTransitive && GLOBAL_ENTITIES.contains(entityType)));
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        // Always deny access to discipline incident stuff
        return false;
    }
}
