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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates the context of a teacher to see the requested set of graduation plan
 * entities. Returns true if the teacher can see ALL of the entities, and false otherwise.
 *
 * @author shalka
 */
@Component
public class TeacherToGraduationPlanValidator extends AbstractContextValidator {

    @Autowired
    private TeacherToStudentValidator teacherToStudentValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.GRADUATION_PLAN.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.GRADUATION_PLAN, entityType, ids)) {
            return false;
        }

        Set<String> idsToValidate = new HashSet<String>(ids);
        idsToValidate.removeAll(validatedThroughEducationOrganization(idsToValidate));

        if (idsToValidate.isEmpty()) {
            return true;
        }

        return validateThroughStudents(idsToValidate);
    }

    /**
     * Returns the set of graduation plans that have been validated by direct association to the
     * teacher's school.
     *
     * @param ids
     *            Set of graduation plan _id's to be validated.
     * @return Set of graduation plan _id's that have been validated.
     */
    private Set<String> validatedThroughEducationOrganization(Set<String> ids) {
        Set<String> validated = new HashSet<String>();
        Set<String> lineage = getTeacherEdorgLineage();
        NeutralQuery graduationPlanQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, ids, false));
        graduationPlanQuery.addCriteria(new NeutralCriteria(ParameterConstants.EDUCATION_ORGANIZATION_ID,
                NeutralCriteria.CRITERIA_IN, lineage));
        Iterable<Entity> graduationPlans = getRepo().findAll(EntityNames.GRADUATION_PLAN, graduationPlanQuery);

        for (Entity graduationPlan : graduationPlans) {
            validated.add(graduationPlan.getEntityId());
        }

        return validated;
    }

    /**
     * Uses the specified set of graduation plan _id's to look up student school associations (that
     * reference the graduation plans). Then finds studentId on each student school association, and
     * runs the teacher -> student validator to determine if the teacher has context to the
     * specified graduation plans.
     *
     * @param ids
     *            Set of graduation plan _id's to be validated.
     * @return Boolean indicating teacher can access specified graduation plans.
     */
    private boolean validateThroughStudents(Set<String> ids) {
        Set<String> idsToValidate = new HashSet<String>(ids);
        NeutralQuery studentSchoolAssociationQuery = new NeutralQuery(new NeutralCriteria(
                ParameterConstants.GRADUATION_PLAN_ID,
                NeutralCriteria.CRITERIA_IN, ids));
        Iterable<Entity> associations = getRepo().findAll(EntityNames.STUDENT_SCHOOL_ASSOCIATION,
                studentSchoolAssociationQuery);

        for (Entity association : associations) {
            if (idsToValidate.isEmpty()) {
                break;
            }

            String studentId = (String) association.getBody().get(ParameterConstants.STUDENT_ID);
            if (teacherToStudentValidator.validate(EntityNames.STUDENT, new HashSet<String>(Arrays.asList(studentId)))) {
                String graduationPlanId = (String) association.getBody().get(ParameterConstants.GRADUATION_PLAN_ID);
                idsToValidate.remove(graduationPlanId);
            }
        }

        return idsToValidate.isEmpty();
    }
}
