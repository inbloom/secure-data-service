package org.slc.sli.api.security.context.validator;

import org.apache.commons.lang.StringUtils;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
        if (!areParametersValid(EntityNames.PROGRAM, entityType, ids)) {
            return false;
        }
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("staffId","=",SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        nq.addCriteria(new NeutralCriteria("programId", "in", ids));
        addEndDateToQuery(nq, false);
        Iterable<Entity> entities = getRepo().findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);

        Set<String> validIds = new HashSet<String>();
        for (Entity entity : entities) {
            validIds.add((String) entity.getBody().get("programId"));
        }

        return validIds.containsAll(ids);
    }

}
