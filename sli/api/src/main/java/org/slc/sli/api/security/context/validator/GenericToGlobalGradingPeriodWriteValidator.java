package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates Write context to a global grading period.
 */
@Component
public class GenericToGlobalGradingPeriodWriteValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && EntityNames.GRADING_PERIOD.equals(entityType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.GRADING_PERIOD, entityType, ids)) {
            return false;
        }

        /*
         * While we only only expect one ID will be passed in (as this is
         * intended for write validation) we will still be doing the logic to
         * handle a set of IDs
         *
         * Make sure that the grading periods passed in are referenced by a
         * session that is tied to your edorg hierarchy
         */
        Set<String> edOrgLineage = getEdorgLineage(getDirectEdorgs());
        Set<String> gradingPeriodsToValidate = new HashSet<String>(ids);
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.GRADING_PERIOD_REFERENCE,
                NeutralCriteria.CRITERIA_IN, ids));
        Iterable<Entity> sessions = repo.findAll(entityType, query);
        for (Entity session : sessions) {
            if (edOrgLineage.contains(session.getBody().get(ParameterConstants.SCHOOL_ID))) {
                gradingPeriodsToValidate.removeAll((Set<String>) session.getBody().get(
                        ParameterConstants.GRADING_PERIOD_REFERENCE));
                if (gradingPeriodsToValidate.isEmpty()) {
                    // All Grading Period Ids have been validated, return success
                    return true;
                }
            }
        }
        return false;
    }

}
