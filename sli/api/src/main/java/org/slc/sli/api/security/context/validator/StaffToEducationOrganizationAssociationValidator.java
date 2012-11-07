package org.slc.sli.api.security.context.validator;

import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Decides if given user has access to given staffEdorgAssoc
 * 
 * @author dkornishev
 *
 */
@Component
public class StaffToEducationOrganizationAssociationValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.STAFF_ED_ORG_ASSOCIATION.equals(entityType) && isStaff();
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
		if(!canValidate(entityType, true)) {
			throw new IllegalArgumentException("Asked to validate incorrect entity type: "+entityType);
		}
		
		info("Validating {}'s access to staffEducationOrganizationAssoc: [{}]",SecurityUtil.getSLIPrincipal().getName(),ids);
		
		Set<String> lineage = this.getStaffEdOrgLineage();
		
		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id","in",ids,false));
		nq.addCriteria(new NeutralCriteria("body.educationOrganizationReference", "in", lineage,false));
		
		List<Entity> found = (List<Entity>) getRepo().findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, nq);
		
		return ids.size()==found.size();
	}
}
