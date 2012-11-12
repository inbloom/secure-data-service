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

import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.springframework.stereotype.Component;

@Component
public class GenericToStaffValidator extends AbstractContextValidator {
    
    @Override
    //Validates both teacher to staff and staff to staff
    public boolean canValidate(String entityType, boolean isTransitive) {
        
        //For now we'll disable for teachers, but logic should be the same for both staff and teacher
        return !isTransitive && EntityNames.STAFF.equals(entityType) && isStaff();
    }
    
    @Override
    public boolean validate(String entityName, Set<String> staffIds) {
        String myself = SecurityUtil.getSLIPrincipal().getEntity().getEntityId();
        for (String staffId : staffIds) {
            if (!staffId.equals(myself)) {
                return false;
            }
        }
        
        return true;
        
    }
}
