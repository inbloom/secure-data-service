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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;

@Component
public class StudentToSubStudentValidator extends AbstractContextValidator {

    protected static final Set<String> SUB_STUDENT_ENTITIES = new HashSet<String>(Arrays.asList(
            EntityNames.ATTENDANCE,
            EntityNames.STUDENT_ACADEMIC_RECORD,
            EntityNames.STUDENT_ASSESSMENT,
            EntityNames.STUDENT_GRADEBOOK_ENTRY,
            EntityNames.STUDENT_SCHOOL_ASSOCIATION,
            EntityNames.GRADE,
            EntityNames.REPORT_CARD));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return SUB_STUDENT_ENTITIES.contains(entityType) && isStudentOrParent();
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(SUB_STUDENT_ENTITIES, entityType, ids)) {
            return Collections.emptySet();
        }

        // Get the Student IDs on the things we want to see, compare with the IDs of yourself
        Set<String> studentIds = new HashSet<String>(getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(ids), ParameterConstants.STUDENT_ID));
        studentIds.retainAll(getDirectStudentIds());

        return studentIds;
    }
}
