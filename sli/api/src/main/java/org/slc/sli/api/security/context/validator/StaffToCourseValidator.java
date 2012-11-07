package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;


@Component
public class StaffToCourseValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.COURSE.equals(entityType) && isStaff();
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
		Set<String> lineage = this.getStaffEdOrgLineage();

		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", ids));
		nq.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.CRITERIA_IN, lineage));
		return getRepo().count(EntityNames.COURSE, nq) == ids.size();
	}
}
