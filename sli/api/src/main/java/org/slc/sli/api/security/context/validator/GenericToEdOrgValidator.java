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
import java.util.Collections;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;

/**
 * Validates the context of a user to see the requested set of education organizations. Returns
 * true if the user can see ALL of the entities, and false otherwise.
 */
@Component
public class GenericToEdOrgValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        if (EntityNames.SCHOOL.equals(entityType) || EntityNames.EDUCATION_ORGANIZATION.equals(entityType)) {
            if (isStudentOrParent()) {
                // only validates non-transitive url for students and parents
                return !isTransitive;
            }
            return true;
        }
        return false;
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(Arrays.asList(EntityNames.SCHOOL, EntityNames.EDUCATION_ORGANIZATION), entityType, ids)) {
            return Collections.emptySet();
        }

        Set<String> edOrgs = getDirectEdorgs();
        edOrgs.addAll(getEdorgDescendents(edOrgs));
        edOrgs.retainAll(ids);
        return edOrgs;
    }
}
