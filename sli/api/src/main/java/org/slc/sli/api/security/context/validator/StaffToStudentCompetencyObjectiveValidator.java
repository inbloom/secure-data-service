/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.security.context.validator;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Validates the context of a staff member to see the requested set of student competency objectives.
 * Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class StaffToStudentCompetencyObjectiveValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STUDENT_COMPETENCY_OBJECTIVE.equals(entityType) && isStaff();
    }

    @Override
    public boolean validate(String entityType, Set<String> objectiveIds) {
        if (!areParametersValid(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, entityType, objectiveIds)) {
            return false;
        }
        
        Set<String> lineage = this.getStaffEdOrgLineage();
        lineage.addAll(this.getStaffEdOrgParents());
        
        /*
         * Check if the entities being asked for exist in the repo
         * This is done by checking sizes of the input set and
         * the return from the database
         * 
         * Restriction for edorg lineage is added since competency objectives
         * can exist at higher edorgs
         */
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", objectiveIds));
        nq.addCriteria(new NeutralCriteria("educationOrganizationId", NeutralCriteria.CRITERIA_IN, lineage));
        return getRepo().count(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, nq) == objectiveIds.size();
    }

}
