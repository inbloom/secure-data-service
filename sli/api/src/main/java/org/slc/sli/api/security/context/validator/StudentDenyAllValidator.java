package org.slc.sli.api.security.context.validator;

import org.slc.sli.common.constants.EntityNames;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class StudentDenyAllValidator extends AbstractContextValidator {

    protected static final Set<String> STUDENT_DENIED_ENTITIES = new HashSet<String>(Arrays.asList(
            EntityNames.DISCIPLINE_ACTION,
            EntityNames.DISCIPLINE_INCIDENT,
            EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION));


    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent() && STUDENT_DENIED_ENTITIES.contains(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        // Always deny access to discipline incident stuff
        return false;
    }
}
