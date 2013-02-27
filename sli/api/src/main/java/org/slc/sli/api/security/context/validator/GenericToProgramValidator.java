package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

@Component
public class GenericToProgramValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.PROGRAM.equals(entityType) && !isTransitive;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.PROGRAM, entityType, ids)) {
            return false;
        }
        NeutralCriteria studentCriteria = new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true);

        Set<String> programsToValidate = new HashSet<String>(ids);

        // Fetch associations
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("body.staffId", "=", SecurityUtil.getSLIPrincipal().getEntity().getEntityId(), false));
        nq.addCriteria(studentCriteria);
        addEndDateToQuery(nq, false);

        Iterable<Entity> assocs = getRepo().findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);
        for (Entity assoc : assocs) {
        	programsToValidate.remove((String) assoc.getBody().get("programId"));
        }

        return programsToValidate.isEmpty();
    }
}