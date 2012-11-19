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
 * Decides if given user has access to given program
 * 
 * @author dkornishev
 * 
 */
@Component
public class StaffToProgramValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.PROGRAM.equals(entityType) && isStaff();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean validate(String entityType, Set<String> ids) {
        return validateWithStudentAccess(entityType, ids, false);
    }
    
    public boolean validateWithStudentAccess(String entityType, Set<String> ids, boolean byStudentAccess) {
        if (!canValidate(entityType, true)) {
			throw new IllegalArgumentException("Asked to validate incorrect entity type: " + entityType);
		}
        
        if (ids.size() == 0) {
            return false;
        }

		info("Validating {}'s access to Programs: [{}]", SecurityUtil.getSLIPrincipal().getName(), ids);
        NeutralCriteria studentCriteria = new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true);
		Set<String> lineage = this.getStaffEdOrgLineage();

		// Fetch programs of your edorgs
		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", lineage, false));
		Iterable<Entity> edorgs = getRepo().findAll(EntityNames.EDUCATION_ORGANIZATION, nq);

		Set<String> allowedIds = new HashSet<String>();
		for (Entity ed : edorgs) {
			List<String> programs = (List<String>) ed.getBody().get("programReference");

			if (programs != null && programs.size() > 0) {
				allowedIds.addAll(programs);
			}

		}

		// Fetch associations
		nq = new NeutralQuery(new NeutralCriteria("body.staffId", "=", SecurityUtil.getSLIPrincipal().getEntity().getEntityId(),false));
        if (byStudentAccess) {
            nq.addCriteria(studentCriteria);
        }
		Iterable<Entity> assocs = getRepo().findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);

		for (Entity assoc : assocs) {
			allowedIds.add((String) assoc.getBody().get("programId"));
		}
		
		
		return allowedIds.containsAll(ids);
    }
}
