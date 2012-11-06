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

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates the context of a staff member to see the requested set of sections. Returns true if the
 * staff member can see ALL of the sections, and false otherwise.
 */
@Component
public class StaffToSectionValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return through == false && entityType.equals(EntityNames.SECTION) && isStaff();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        boolean match = false;
        Set<String> edorgLineage = getStaffEdOrgLineage();

        for (String id : ids) {
        	NeutralQuery basicQuery = new NeutralQuery(
                    new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, id));
            Entity section = getRepo().findOne(EntityNames.SECTION, basicQuery);
            if (section == null) {
                return false;
            }
            String schoolId = (String) section.getBody().get(ParameterConstants.SCHOOL_ID);
            if (!edorgLineage.contains(schoolId)) {
                return false;
            } else {
                match = true;
            }
        }
        return match;
    }

}
