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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validates the context of a staff member to see the requested set of grades. Returns true if the
 * staff member can see ALL of the grades, and false otherwise.
 *
 * @author shalka
 */
@Component
public class StaffToSubStudentSectionAssociationEntityValidator extends AbstractContextValidator {

    @Autowired
    private StaffToStudentValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return isStaff() && isSubEntityOfStudentSectionAssociation(entityType);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(SUB_ENTITIES_OF_STUDENT_SECTION, entityType, ids)) {
            return Collections.EMPTY_SET;
        }
        
        Map<String, Set<String>> studentSectionAssociationIds = getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(
                ids), ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);
        if (studentSectionAssociationIds.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Map<String, Set<String>> studentIds = getIdsContainedInFieldOnEntities(EntityNames.STUDENT_SECTION_ASSOCIATION,
                new ArrayList<String>(studentSectionAssociationIds.keySet()), ParameterConstants.STUDENT_ID);
        if (studentIds.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> validStudents = validator.validate(EntityNames.STUDENT, studentIds.keySet());
        return getValidIds(validStudents, studentIds);
    }

    /**
     * Sets the staff to student validator (for unit testing).
     *
     * @param staffToStudentValidator
     *            Staff To Student Validator.
     */
    protected void setStaffToStudentValidator(StaffToStudentValidator staffToStudentValidator) {
        this.validator = staffToStudentValidator;
    }
}
