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

import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;

/**
 * Validates the context of a teacher to see the requested set of education organizations.
 * Returns true if the teacher member can see ALL of the entities, and false otherwise.
 */
@Component
public class TeacherToEdOrgValidator extends AbstractContextValidator {

    @Autowired
    EdOrgHelper helper;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isTransitive
                && (EntityNames.SCHOOL.equals(entityType) || EntityNames.EDUCATION_ORGANIZATION.equals(entityType))
                && isTeacher();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(Arrays.asList(EntityNames.SCHOOL, EntityNames.EDUCATION_ORGANIZATION), entityType, ids)) {
            return false;
        }

        Set<String> schools = getDirectEdorgs();
        schools.addAll(getEdorgLineage(schools));
        return schools.containsAll(ids);
    }

}
