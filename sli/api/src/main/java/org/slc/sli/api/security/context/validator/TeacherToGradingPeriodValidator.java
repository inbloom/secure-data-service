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
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Validate grading periods.
 * 
 * Verifies that for each grading period there's at least one session in which the
 * user has access to based on edorg lineage.
 */
@Component
public class TeacherToGradingPeriodValidator extends AbstractContextValidator {
    
    @Autowired PagingRepositoryDelegate<Entity> repo;
    

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.GRADING_PERIOD.equals(entityType) && isTeacher();
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.GRADING_PERIOD, entityType, ids)) {
            return false;
        }

        Set<String> edorgs = getTeacherEdorgLineage();

        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.GRADING_PERIOD_REFERENCE,
                NeutralCriteria.CRITERIA_IN, ids));
		nq.addCriteria(new NeutralCriteria(ParameterConstants.SCHOOL_ID, NeutralCriteria.CRITERIA_IN, edorgs));
        nq.setIncludeFields(Arrays.asList(ParameterConstants.GRADING_PERIOD_REFERENCE));
		Iterable<Entity> entities = getRepo().findAll(EntityNames.SESSION, nq);

		Set<String> validGradingPeriods = new HashSet<String>();
		
		for (Entity session : entities) {
            validGradingPeriods.addAll((Collection<String>) session.getBody().get(
                    ParameterConstants.GRADING_PERIOD_REFERENCE));
		}

		return validGradingPeriods.containsAll(ids);
	}

}
