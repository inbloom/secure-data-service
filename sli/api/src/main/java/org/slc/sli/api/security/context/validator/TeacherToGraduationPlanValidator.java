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

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates the context of a teacher to see the requested set of graduation plan
 * entities. Returns true if the teacher can see ALL of the entities, and false otherwise.
 *
 * @author shalka
 */
@Component
public class TeacherToGraduationPlanValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isTransitive && isTeacher() && EntityNames.GRADUATION_PLAN.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.GRADUATION_PLAN, entityType, ids)) {
            return false;
        }

        Set<String> lineage = getTeacherEdorgLineage();
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids, false));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.EDUCATION_ORGANIZATION_ID, NeutralCriteria.CRITERIA_IN,
                lineage));
        Iterable<Entity> graduationPlans = getRepo().findAll(EntityNames.GRADUATION_PLAN, nq);

        Set<String> valid = new HashSet<String>();
        for (Entity graduationPlan : graduationPlans) {
            valid.add(graduationPlan.getEntityId());
        }

        return valid.containsAll(ids) && valid.size() == ids.size();
    }
}
