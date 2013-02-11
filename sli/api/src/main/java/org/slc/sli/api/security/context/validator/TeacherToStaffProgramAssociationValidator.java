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
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Validates teacher's access to given teacherProgramAssociation
 * Teacher can see his/hers teacherProgramAssociations
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherToStaffProgramAssociationValidator extends AbstractContextValidator {

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.STAFF_PROGRAM_ASSOCIATION.equals(entityType) && isTeacher();
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.STAFF_PROGRAM_ASSOCIATION, entityType, ids)) {
            return false;
        }

        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
		
		long count = getRepo().count(EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);
		return count == ids.size();
	}

}
