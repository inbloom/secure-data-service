package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * This validator validates Write access to programs: this is defined as having
 * a direct association to the program, or being directly associated to an edorg
 * which references the program
 * 
 * This logic is applied for both teachers and staff
 * 
 */
@Component
public class GenericToGlobalProgramWriteValidator extends
		AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.PROGRAM.equals(entityType) && isTransitive;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(String entityType, Set<String> ids)
			throws IllegalStateException {
		if (!areParametersValid(EntityNames.PROGRAM, entityType, ids)) {
            return false;
        }
		Set<String> directEdorgs = getDirectEdorgs();

		// Fetch programs of your edorgs
		NeutralQuery nq = new NeutralQuery(new NeutralCriteria(
				ParameterConstants.ID, NeutralCriteria.CRITERIA_IN,
				directEdorgs, false));
		Iterable<Entity> edorgs = getRepo().findAll(
				EntityNames.EDUCATION_ORGANIZATION, nq);

		Set<String> programsToValidate = new HashSet<String>(ids);

		for (Entity ed : edorgs) {
			List<String> programs = (List<String>) ed.getBody().get(
					ParameterConstants.PROGRAM_REFERENCE);

			if (programs != null && programs.size() > 0) {
				programsToValidate.removeAll(programs);
				if (programsToValidate.isEmpty()) {
					return true;
				}
			}

		}

		// Fetch associations
		nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID,
				NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal()
						.getEntity().getEntityId()));
		Iterable<Entity> assocs = getRepo().findAll(
				EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);

		for (Entity assoc : assocs) {
			programsToValidate.remove((String) assoc.getBody().get(
					ParameterConstants.PROGRAM_ID));
			if (programsToValidate.isEmpty()) {
				return true;
			}
		}

		// If we made it this far, there's still programs that didn't validate,
		// return false
		return false;
	}

}
