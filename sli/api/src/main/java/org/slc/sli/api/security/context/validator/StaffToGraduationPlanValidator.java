package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

@Component
public class StaffToGraduationPlanValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.GRADUATION_PLAN.equals(entityType) && isStaff();
    }

    @Override
    public boolean validate(String entityType, Set<String> graduationIds) {
    	Set<String> lineage = this.getStaffEdOrgLineage();
    	lineage.addAll(this.getStaffEdOrgParents());
    	
        /*
         * Check if the entities being asked for exist in the repo
         * This is done by checking sizes of the input set and
         * the return from the database
         * 
         * Restriction for edorg lineage is added since graduation plans
         * can exist at higher edorgs
         */
		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", graduationIds));
		nq.addCriteria(new NeutralCriteria("educationOrganizationId", NeutralCriteria.CRITERIA_IN, lineage));
		return getRepo().count(EntityNames.GRADUATION_PLAN, nq) == graduationIds.size();
    }

}
