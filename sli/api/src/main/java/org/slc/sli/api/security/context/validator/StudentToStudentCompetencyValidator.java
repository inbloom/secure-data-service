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

import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;

@Component
public class StudentToStudentCompetencyValidator extends AbstractContextValidator{

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent() && EntityNames.STUDENT_COMPETENCY.equals(entityType);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_COMPETENCY, entityType, ids)) {
            return Collections.emptySet();
        }

        Map<String, Set<String>> ssaToSC = getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(
                ids), ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);
        if (ssaToSC.isEmpty()) {
            return Collections.emptySet();
        }

        // We cannot chain to the studentSectionAssociation validator, since you have context
        // to more SSA than the grades on those SSA, so get the student IDs and compare to yourself
        Map<String, Set<String>> studentIdsToSSA = getIdsContainedInFieldOnEntities(EntityNames.STUDENT_SECTION_ASSOCIATION,
                new ArrayList<String>(ssaToSC.keySet()), ParameterConstants.STUDENT_ID);

        if (studentIdsToSSA.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> studentIds = studentIdsToSSA.keySet();
        studentIds.retainAll(getDirectStudentIds());

        Set<String> validSSAIds = getValidIds(studentIds, studentIdsToSSA);
        Set<String> validSCIds = getValidIds(validSSAIds, ssaToSC);
        return validSCIds;
    }
}
