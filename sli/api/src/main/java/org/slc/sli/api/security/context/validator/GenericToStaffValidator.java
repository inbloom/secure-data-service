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
import org.slc.sli.api.util.SecurityUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * Resolves which staff any generic entity can access.
 * 
 * @author kmyers
 *
 */
@Component
public class GenericToStaffValidator extends AbstractContextValidator {
    
    @Override
    //Validates both teacher to staff and staff to staff and teacher to teacher
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isTransitive && (EntityNames.STAFF.equals(entityType) || 
                (isTeacher() && EntityNames.TEACHER.equals(entityType)));
    }
    
    @Override
    public boolean validate(String entityName, Set<String> staffIds) {
        if (!areParametersValid(Arrays.asList(EntityNames.STAFF, EntityNames.TEACHER), entityName, staffIds)) {
            return false;
        }
        
        if (staffIds.size() > 1) {
            return false;
        }
        
        String myself = SecurityUtil.principalId();
        
        //will only be one staffId in the list
        return staffIds.contains(myself);
        
    }
}
