package org.slc.sli.api.security.context.validator;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class StudentToStudentCompetencyValidator extends AbstractContextValidator{

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent() && EntityNames.STUDENT_COMPETENCY.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_COMPETENCY, entityType, ids)) {
            return false;
        }

        List<String> studentSectionAssociationIds = getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(
                ids), ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);
        if (studentSectionAssociationIds.isEmpty()) {
            return false;
        }

        // We cannot chain to the studentSectionAssociation validator, since you have context
        // to more SSA than the grades on those SSA, so get the student IDs and compare to yourself
        List<String> studentIds = getIdsContainedInFieldOnEntities(EntityNames.STUDENT_SECTION_ASSOCIATION,
                studentSectionAssociationIds, ParameterConstants.STUDENT_ID);
        if (studentIds.isEmpty()) {
            return false;
        }

        return getDirectStudentIds().containsAll(studentIds);
    }
}
