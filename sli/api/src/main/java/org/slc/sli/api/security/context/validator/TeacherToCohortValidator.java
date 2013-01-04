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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates teacher's access to given cohorts
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherToCohortValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.COHORT.equals(entityType) && isTeacher();
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.COHORT, entityType, ids)) {
            return false;
        }
		
		if(ids==null || ids.size()==0) {
			throw new IllegalArgumentException("Incoming list of ids cannot be null");
		}
 
		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("staffId","=",SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
		nq.addCriteria(new NeutralCriteria("cohortId", "in", ids));

        Iterable<Entity> entities = getRepo().findAll(EntityNames.STAFF_COHORT_ASSOCIATION, nq);

        Set<String> validIds = new HashSet<String>();
        for (Entity entity : entities) {
            validIds.add((String) entity.getBody().get("cohortId"));
        }

        return validIds.containsAll(ids);
	}

}
