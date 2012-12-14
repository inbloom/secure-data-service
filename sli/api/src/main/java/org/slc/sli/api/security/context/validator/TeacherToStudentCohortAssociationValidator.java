package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates teacher's access to given student cohort assocs.
 * 
 */
@Component
public class TeacherToStudentCohortAssociationValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STUDENT_COHORT_ASSOCIATION.equals(entityType) && isTeacher();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.STUDENT_COHORT_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        //Get all the cohort IDs from the associations passed in
        //And ensure teacher has direct staffCohortAssocation for each of those
        
        NeutralQuery query = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        query.setIncludeFields(Arrays.asList(ParameterConstants.COHORT_ID));
        
        Set<String> cohortIds = new HashSet<String>();
        
        Iterable<Entity> scas = getRepo().findAll(EntityNames.STUDENT_COHORT_ASSOCIATION, query);
        for (Entity sca : scas) {
            cohortIds.add((String) sca.getBody().get(ParameterConstants.COHORT_ID));
        }
        
        String teacherId = SecurityUtil.getSLIPrincipal().getEntity().getEntityId();
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.COHORT_ID, NeutralCriteria.CRITERIA_IN, cohortIds));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL, teacherId));
        
        long result = getRepo().count(EntityNames.STAFF_COHORT_ASSOCIATION, nq);
        return result == ids.size();
    }

}
