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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;

@Component
public class StudentToStudentAssociationValidator extends AbstractContextValidator{

    @Autowired
    private TransitiveStudentToStudentValidator studentValidator;

    protected static final Set<String> STUDENT_ASSOCIATIONS = new HashSet<String>(Arrays.asList(
            EntityNames.STUDENT_SECTION_ASSOCIATION,
            EntityNames.STUDENT_COHORT_ASSOCIATION,
            EntityNames.STUDENT_PROGRAM_ASSOCIATION));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent() && STUDENT_ASSOCIATIONS.contains(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(STUDENT_ASSOCIATIONS, entityType, ids)) {
            return false;
        }

        // Get the Student IDs on the things we want to see,
        // then call the transitive student validator to see if you have access to those students
        Set<String> studentIds = new HashSet<String>(getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(ids), ParameterConstants.STUDENT_ID));

        return studentValidator.validate(EntityNames.STUDENT, studentIds);
    }
}
