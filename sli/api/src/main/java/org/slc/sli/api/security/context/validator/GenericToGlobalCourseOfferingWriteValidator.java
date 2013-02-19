package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

@Component
public class GenericToGlobalCourseOfferingWriteValidator extends
		AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return isTransitive && EntityNames.COURSE_OFFERING.equals(entityType);
	}

	@Override
	public boolean validate(String entityType, Set<String> ids)
			throws IllegalStateException {
		if (!areParametersValid(EntityNames.COURSE_OFFERING, entityType, ids)) {
			return false;
		}

		/*
		 * Grab all the Course Offerings that are being requested AND contain a
		 * reference to a school in your edorg hierarchy. Counts should be equal
		 * if you can see all the course offerings you asked for
		 */
		Set<String> edOrgLineage = getEdorgLineage(getDirectEdorgs());
		NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id",
				NeutralCriteria.CRITERIA_IN, ids, false));
		query.addCriteria(new NeutralCriteria("schoolId",
				NeutralCriteria.CRITERIA_IN, edOrgLineage));

		return ids.size() == repo.count(entityType, query);
	}

}
