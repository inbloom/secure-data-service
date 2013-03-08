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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates teacher's access to given student program assocs.
 * 
 */
@Component
public class TeacherToStudentProgramAssociationValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STUDENT_PROGRAM_ASSOCIATION.equals(entityType) && isTeacher();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_PROGRAM_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        //Get all the cohort IDs from the associations passed in
        //And ensure teacher has direct staffCohortAssocation for each of those
        
        NeutralQuery query = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        query.setIncludeFields(Arrays.asList(ParameterConstants.PROGRAM_ID));
        
        Set<String> programIds = new HashSet<String>();
        
        Iterable<Entity> scas = getRepo().findAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, query);
        for (Entity sca : scas) {
            programIds.add((String) sca.getBody().get(ParameterConstants.PROGRAM_ID));
        }
        
        String teacherId = SecurityUtil.getSLIPrincipal().getEntity().getEntityId();
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.PROGRAM_ID, NeutralCriteria.CRITERIA_IN, programIds));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL, teacherId));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        
        Iterable<Entity> entities = getRepo().findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);

        Set<String> validProgramIds = new HashSet<String>();
        for (Entity entity : entities) {
            String expireDate = (String) entity.getBody().get(ParameterConstants.END_DATE);
            if (expireDate == null || isLhsBeforeRhs(getNowMinusGracePeriod(), getDateTime(expireDate))) {
                validProgramIds.add((String) entity.getBody().get(ParameterConstants.PROGRAM_ID));
            }
            
        }

        return validProgramIds.containsAll(programIds);
    }

}
