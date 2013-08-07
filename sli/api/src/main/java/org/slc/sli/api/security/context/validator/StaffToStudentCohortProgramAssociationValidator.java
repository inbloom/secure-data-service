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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validates the context of a staff member to see the requested set of student cohort or program associations.
 * Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class StaffToStudentCohortProgramAssociationValidator extends AbstractContextValidator {
        
    @Autowired
    private StaffToStudentValidator studentValidator;

    protected static final Set<String> STUDENT_ASSOCIATIONS = new HashSet<String>(Arrays.asList(
            EntityNames.STUDENT_COHORT_ASSOCIATION,
            EntityNames.STUDENT_PROGRAM_ASSOCIATION));


    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return STUDENT_ASSOCIATIONS.contains(entityType) && isStaff();
    }
    
    /**
     * Can see all of the studentCohortAssociations or studentProgramAssociations of the students I can see
     * provided they aren't expired.
     */
    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(STUDENT_ASSOCIATIONS, entityType, ids)) {
            return Collections.EMPTY_SET;
        }
        Map<String, Set<String>> associations = new HashMap<String, Set<String>>();
        // See the student
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, ids));
        Iterable<Entity> assocs = getRepo().findAll(entityType, basicQuery);
        for (Entity assoc : assocs) {
            String studentId = (String) assoc.getBody().get(ParameterConstants.STUDENT_ID);
            if (!isFieldExpired(assoc.getBody(), ParameterConstants.END_DATE, true)) {
                if (!associations.containsKey(studentId)) {
                    associations.put(studentId, new HashSet<String>());
                }
                associations.get(studentId).add(assoc.getEntityId());
            }
        }
        Set<String> validStudents = studentValidator.validate(EntityNames.STUDENT, associations.keySet());
        return getValidIds(validStudents, associations);
    }
        
}
