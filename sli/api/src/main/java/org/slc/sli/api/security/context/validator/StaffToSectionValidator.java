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
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Validates the context of a staff member to see the requested set of sections. Returns true if the
 * staff member can see ALL of the sections, and false otherwise.
 */
@Component
public class StaffToSectionValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return EntityNames.SECTION.equals(entityType) && isStaff();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.SECTION, entityType, ids)) {
            return false;
        }
        
        Set<String> edorgLineage = getStaffEdOrgLineage();
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, ids));
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.SCHOOL_ID, NeutralCriteria.CRITERIA_IN,
                edorgLineage));
        return getRepo().count(EntityNames.SECTION, basicQuery) == ids.size();
    }

}
