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

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;

@Component
public class StudentDenyAllValidator extends AbstractContextValidator {

    protected static final Set<String> STUDENT_DENIED_ENTITIES = new HashSet<String>(Arrays.asList(
            EntityNames.DISCIPLINE_ACTION,
            EntityNames.DISCIPLINE_INCIDENT,
            EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION));

    private static final Set<String> NON_TRANSITIVE_DENY_ALL = new HashSet<String>(Arrays.asList(
            EntityNames.STAFF,
            EntityNames.TEACHER
            ));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent()
                && (STUDENT_DENIED_ENTITIES.contains(entityType)
                        || (isTransitive && EntityNames.isPublic(entityType)) || (!isTransitive && NON_TRANSITIVE_DENY_ALL
                        .contains(entityType)));
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        // Always deny access to discipline incident stuff
        return false;
    }
}
