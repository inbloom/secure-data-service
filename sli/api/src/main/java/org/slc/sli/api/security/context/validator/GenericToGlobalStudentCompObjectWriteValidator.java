package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

@Component
public class GenericToGlobalStudentCompObjectWriteValidator extends
		AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return isTransitive && EntityNames.STUDENT_COMPETENCY_OBJECTIVE.equals(entityType);
	}

	@Override
	public boolean validate(String entityType, Set<String> ids)
			throws IllegalStateException {
		if (!areParametersValid(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, entityType, ids)) {
			return false;
		}

		/*
		 * Grab all the Competency Objs that are being requested AND contain a
		 * reference to a edorg in your edorg hierarchy. Counts should be equal
		 * if you can see all the competency objs you asked for
		 */
		Set<String> edOrgLineage = getEdorgLineage(getDirectEdorgs());
		NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id",
				NeutralCriteria.CRITERIA_IN, ids, false));
		query.addCriteria(new NeutralCriteria("educationOrganizationId",
				NeutralCriteria.CRITERIA_IN, edOrgLineage));

		return ids.size() == repo.count(entityType, query);
	}

}
