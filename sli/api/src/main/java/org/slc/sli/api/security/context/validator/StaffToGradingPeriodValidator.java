/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates the context of a staff member to see the requested set of grading period
 * entities. Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class StaffToGradingPeriodValidator extends AbstractContextValidator {

    private static final String GRADING_PERIOD_REFERENCE = "gradingPeriodReference";

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return EntityNames.GRADING_PERIOD.equals(entityType) && isStaff();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(String entityType, Set<String> entityIds) {
        if (!areParametersValid(EntityNames.GRADING_PERIOD, entityType, entityIds)) {
            return false;
        }
        
        Set<String> toResolve = new HashSet<String>(entityIds);
        
        /*
         * Grading period access is granted though the grading period reference on Sessions
         * Grab all the sessions referencing the requested grading periods and validate that
         * you can see those session(s)
         */
        NeutralQuery nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria(GRADING_PERIOD_REFERENCE, NeutralCriteria.CRITERIA_IN, entityIds));
        Iterable<Entity> entities = getRepo().findAll(EntityNames.SESSION, nq);
        
        for (Entity session : entities) {
            /*
             * Minor optimization, make sure the session you're going to resolve references
             * a grading period you still need to validate access to.
             * This will prevent unneeded calls to the session validator, which could get expensive
             */
            List<String> gradingPeriodRefs = (List<String>) session.getBody().get(GRADING_PERIOD_REFERENCE);
            gradingPeriodRefs.retainAll(toResolve);
            
            if (!gradingPeriodRefs.isEmpty()) {
                boolean validated = validateSessionIds(EntityNames.SESSION,
                        new HashSet<String>(Arrays.asList(session.getEntityId())));
                if (validated) {
                    toResolve.removeAll(gradingPeriodRefs);
                    if (toResolve.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    private boolean validateSessionIds(String entityType, Set<String> entityIds) {
        if (!areParametersValid(EntityNames.SESSION, entityType, entityIds)) {
            return false;
        }

        Set<String> lineage = this.getStaffEdOrgLineage();
        lineage.addAll(this.getStaffEdOrgParents());

        /*
         * Check if the entities being asked for exist in the repo
         * This is done by checking sizes of the input set and
         * the return from the database
         *
         * Restriction for edorg lineage is added since session access
         * is granted through schools/edorgs
         */
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", entityIds));
        nq.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.CRITERIA_IN, lineage));
        return getRepo().count(EntityNames.SESSION, nq) == entityIds.size();
    }

}
