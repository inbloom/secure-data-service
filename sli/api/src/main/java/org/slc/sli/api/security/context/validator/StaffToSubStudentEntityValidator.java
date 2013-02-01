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
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates a staff accessing a set of entities that are directly associated to a student.
 * Currently supported entities are:
 * attendance,
 * course transcript,
 * discipline action,
 * student academic record,
 * student assessment association,
 * student discipline incident association,
 * student grade book entry,
 * student parent association,
 * student section association,
 * student school association.
 *
 * @author shalka
 */
@Component
public class StaffToSubStudentEntityValidator extends AbstractContextValidator {

    @Autowired
    private StaffToStudentValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return isStaff() && isSubEntityOfStudent(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(SUB_ENTITIES_OF_STUDENT, entityType, ids)) {
            return false;
        }
        
        Set<String> students = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.OPERATOR_EQUAL, new ArrayList<String>(ids)));
        Iterable<Entity> entities = getRepo().findAll(entityType, query);
        if (entities != null) {
            for (Entity entity : entities) {
                Map<String, Object> body = entity.getBody();
                if (entityType.equals(EntityNames.STUDENT_SCHOOL_ASSOCIATION) && body.containsKey("exitWithdrawDate")) {
                    if (isLhsBeforeRhs(getNowMinusGracePeriod(), getDateTime((String) body.get("exitWithdrawDate")))) {
                        students.add((String) body.get(ParameterConstants.STUDENT_ID));
                    }
                } else if (entityType.equals(EntityNames.STUDENT_SECTION_ASSOCIATION) && body.containsKey("endDate")) {
                    if (isLhsBeforeRhs(getNowMinusGracePeriod(), getDateTime((String) body.get("endDate")))) {
                        students.add((String) body.get(ParameterConstants.STUDENT_ID));
                    }
                } else {
                    Object studentInfo = body.get(ParameterConstants.STUDENT_ID);
                    if (studentInfo instanceof Collection) {    //e.g. BasicDBList
                        students.addAll((Collection) studentInfo);
                    } else if (studentInfo instanceof String) {
                        students.add((String) studentInfo);
                    }
                }
            }
        }

        if (students.size() != ids.size()) {
            return false;
        }

        return validator.validate(EntityNames.STUDENT, students);
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
