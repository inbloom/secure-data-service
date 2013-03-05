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

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates the context of a staff member to see the requested set of staff program associations.
 * Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class StaffToStaffProgramAssociationValidator extends AbstractContextValidator {
    
    @Autowired
    private TransitiveStaffToStaffValidator staffValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STAFF_PROGRAM_ASSOCIATION.equals(entityType) && isStaff();
    }
    
    /**
     * You can see all of the staffProgramAssociations that you have and that
     * all of the staff you can see have.
     */
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.STAFF_PROGRAM_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        //Get the ones based on staffIds (Including me)
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        
        Set<String> staffIds = new HashSet<String>();
        Iterable<Entity> staffPrograms = getRepo().findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, basicQuery);
        for (Entity staff : staffPrograms) {
            Map<String, Object> body = staff.getBody();
            if (isFieldExpired(body, ParameterConstants.END_DATE, true)) {
                continue;
            }
            staffIds.add((String) body.get(ParameterConstants.STAFF_ID));
        }
        
        return staffValidator.validate(EntityNames.STAFF, staffIds);
    }
    
}
