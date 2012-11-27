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
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Resolves which course(s) any given staff can access.
 * 
 * @author kmyers
 *
 */
@Component
public class StaffToCourseValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.COURSE.equals(entityType) && isStaff();
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        Set<String> lineage = this.getStaffEdOrgLineage();
        
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", ids));
        nq.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.CRITERIA_IN, lineage));
        return getRepo().count(EntityNames.COURSE, nq) == ids.size();
    }
}
