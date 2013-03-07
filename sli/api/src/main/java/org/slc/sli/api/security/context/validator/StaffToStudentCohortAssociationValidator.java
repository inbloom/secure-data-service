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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates the context of a staff member to see the requested set of student cohort associations.
 * Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class StaffToStudentCohortAssociationValidator extends AbstractContextValidator {
        
    @Autowired
    private StaffToStudentValidator studentValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STUDENT_COHORT_ASSOCIATION.equals(entityType) && isStaff();
    }
    
    /**
     * Can see all of the studentCohortAssociations of the students I can see provided they aren't
     * expired.
     */
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.STUDENT_COHORT_ASSOCIATION, entityType, ids)) {
            return false;
        }
        Set<String> associations = new HashSet<String>();
        // See the student
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, ids));
        Iterable<Entity> scas = getRepo().findAll(EntityNames.STUDENT_COHORT_ASSOCIATION, basicQuery);
        for (Entity sca : scas) {
            String studentId = (String) sca.getBody().get(ParameterConstants.STUDENT_ID);
            if (isFieldExpired(sca.getBody(), ParameterConstants.END_DATE, true)) {
                return false;
            } else {
                associations.add(studentId);
            }
        }
        return studentValidator.validate(EntityNames.STUDENT, associations);
    }
        
}
