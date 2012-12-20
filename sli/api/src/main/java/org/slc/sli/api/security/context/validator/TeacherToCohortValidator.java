package org.slc.sli.api.security.context.validator;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates teacher's access to given cohorts
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherToCohortValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.COHORT.equals(entityType) && isTeacher();
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
		if (!this.canValidate(entityType, false)) {
			throw new IllegalArgumentException(String.format("Asked to validate %s->%s[%s]", SecurityUtil.getSLIPrincipal().getEntity().getType(),entityType,false));
		}
		
		if(ids==null || ids.size()==0) {
			throw new IllegalArgumentException("Incoming list of ids cannot be null");
		}
 
		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("staffId","=",SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
		nq.addCriteria(new NeutralCriteria("cohortId", "in", ids));

        Iterable<Entity> entities = getRepo().findAll(EntityNames.STAFF_COHORT_ASSOCIATION, nq);

        Set<String> validIds = new HashSet<String>();
        for (Entity entity : entities) {
            validIds.add((String) entity.getBody().get("cohortId"));
        }

        return validIds.containsAll(ids);
	}

}
