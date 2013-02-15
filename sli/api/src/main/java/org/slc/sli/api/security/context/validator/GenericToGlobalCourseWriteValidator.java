package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

public class GenericToGlobalCourseWriteValidator extends
		AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return isTransitive && EntityNames.COURSE.equals(entityType);
	}

	@Override
	public boolean validate(String entityType, Set<String> ids)
			throws IllegalStateException {
		if (!areParametersValid(EntityNames.COURSE, entityType, ids)) {
			return false;
		}

		/*
		 * Grab all the Courses that are being requested AND contain a reference
		 * to a school in your edorg hierarchy.  Counts should be equal if
		 * you can see all the courses you asked for
		 */
		Set<String> edOrgLineage = getEdorgLineage(getDirectEdorgs());
		NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id",
				NeutralCriteria.CRITERIA_IN, ids, false));
		query.addCriteria(new NeutralCriteria("schoolId",
				NeutralCriteria.CRITERIA_IN, edOrgLineage));

		return ids.size() == repo.count(entityType, query);
	}

}
