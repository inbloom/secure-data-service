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

/**
 * A public global validator for student to access public entities.
 * 
 */
@Component
public class TransitiveStudentToGlobalValidator extends AbstractContextValidator {
    
    // Student and parent has context to every section in the tenant, as sections are Global
    // entities.
    private static final Set<String> GLOBAL_ACCESS_ALL = new HashSet<String>(Arrays.asList(EntityNames.SECTION));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && GLOBAL_ACCESS_ALL.contains(entityType) && isStudent();
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        return true;
    }
    
}
