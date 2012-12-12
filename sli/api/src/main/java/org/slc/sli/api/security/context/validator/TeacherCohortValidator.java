package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates teacher's access to given cohorts
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherCohortValidator extends AbstractContextValidator {

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
		long result = getRepo().count(EntityNames.STAFF_COHORT_ASSOCIATION, nq);
		return result==ids.size();
	}

}
