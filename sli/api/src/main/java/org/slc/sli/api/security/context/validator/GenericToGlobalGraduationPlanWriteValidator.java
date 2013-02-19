package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

public class GenericToGlobalGraduationPlanWriteValidator extends
		AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return isTransitive && EntityNames.GRADUATION_PLAN.equals(entityType);
	}

	@Override
	public boolean validate(String entityType, Set<String> ids)
			throws IllegalStateException {
		if (!areParametersValid(EntityNames.GRADUATION_PLAN, entityType, ids)) {
			return false;
		}

		/*
		 * Grab all the Graduation Plans that are being requested AND contain a
		 * reference to a edorg in your edorg hierarchy. Counts should be equal
		 * if you can see all the graduation plans you asked for
		 */
		Set<String> edOrgLineage = getEdorgLineage(getDirectEdorgs());
		NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id",
				NeutralCriteria.CRITERIA_IN, ids, false));
		query.addCriteria(new NeutralCriteria("educationOrganizationId",
				NeutralCriteria.CRITERIA_IN, edOrgLineage));

		return ids.size() == repo.count(entityType, query);
	}

}
