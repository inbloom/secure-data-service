package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates teacher's direct access to given programs
 * 
 */
@Component
public class TeacherToProgramValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isTransitive && EntityNames.PROGRAM.equals(entityType) && isTeacher();
    }

    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("staffId","=",SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        nq.addCriteria(new NeutralCriteria("programId", "in", ids));
        addEndDateToQuery(nq, false);
        long result = getRepo().count(EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);
        return result==ids.size();
    }

}
