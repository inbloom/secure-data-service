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
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validates teacher's access to given student cohort assocs.
 * 
 */
@Component
public class TeacherToStudentCohortAssociationValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STUDENT_COHORT_ASSOCIATION.equals(entityType) && isTeacher();
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_COHORT_ASSOCIATION, entityType, ids)) {
            return Collections.EMPTY_SET;
        }
        
        //Get all the cohort IDs from the associations passed in
        //And ensure teacher has direct staffCohortAssocation for each of those
        
        NeutralQuery query = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        query.setIncludeFields(Arrays.asList(ParameterConstants.COHORT_ID));
        
        Map<String, Set<String>> cohortIdToSca = new HashMap<String, Set<String>>();
        Iterable<Entity> scas = getRepo().findAll(EntityNames.STUDENT_COHORT_ASSOCIATION, query);
        for (Entity sca : scas) {
            String cohortId = sca.getBody().get(ParameterConstants.COHORT_ID).toString();
            if(!cohortIdToSca.containsKey(cohortId)) {
                cohortIdToSca.put(cohortId, new HashSet<String>());
            }
            cohortIdToSca.get(cohortId).add(sca.getEntityId());
        }
        
        String teacherId = SecurityUtil.getSLIPrincipal().getEntity().getEntityId();
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.COHORT_ID, NeutralCriteria.CRITERIA_IN, cohortIdToSca.keySet()));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL, teacherId));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        
        Iterable<Entity> entities = getRepo().findAll(EntityNames.STAFF_COHORT_ASSOCIATION, nq);

        Set<String> validCohortIds = new HashSet<String>();
        for (Entity entity : entities) {
            String expireDate = (String) entity.getBody().get(ParameterConstants.END_DATE);
            if (expireDate == null || isLhsBeforeRhs(getNowMinusGracePeriod(), getDateTime(expireDate))) {
                validCohortIds.add((String) entity.getBody().get(ParameterConstants.COHORT_ID));
            }
        }

        return getValidIds(validCohortIds, cohortIdToSca);
    }

}
