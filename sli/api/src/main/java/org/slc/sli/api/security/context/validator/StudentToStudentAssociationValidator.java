package org.slc.sli.api.security.context.validator;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class StudentToStudentAssociationValidator extends AbstractContextValidator{

    @Autowired
    private TransitiveStudentToStudentValidator studentValidator;

    protected static final Set<String> STUDENT_ASSOCIATIONS = new HashSet<String>(Arrays.asList(
            EntityNames.STUDENT_SECTION_ASSOCIATION,
            EntityNames.STUDENT_COHORT_ASSOCIATION,
            EntityNames.STUDENT_PROGRAM_ASSOCIATION));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent() && STUDENT_ASSOCIATIONS.contains(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(STUDENT_ASSOCIATIONS, entityType, ids)) {
            return false;
        }

        // Get the Student IDs on the things we want to see,
        // then call the transitive student validator to see if you have access to those students
        Set<String> studentIds = new HashSet<String>(getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(ids), ParameterConstants.STUDENT_ID));

        return studentValidator.validate(EntityNames.STUDENT, studentIds);
    }
}
