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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates the context of a teacher to see the requested set of grade entities.
 * Returns true if the teacher member can see ALL of the entities, and false otherwise.
 */
@Component
public class TeacherToGradeValidator extends AbstractContextValidator {

    @Autowired
    TeacherToSubStudentEntityValidator sectionAssocValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.GRADE.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.GRADE, entityType, ids)) {
            return false;
        }

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        query.setIncludeFields(Arrays.asList(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID));
        Iterable<Entity> grades = getRepo().findAll(EntityNames.GRADE, query);
        Set<String> secAssocIds = new HashSet<String>();
        for(Entity grade : grades) {
            secAssocIds.add((String) grade.getBody().get(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID));
        }
        return sectionAssocValidator.validate(EntityNames.STUDENT_SECTION_ASSOCIATION, secAssocIds);
    }
}
