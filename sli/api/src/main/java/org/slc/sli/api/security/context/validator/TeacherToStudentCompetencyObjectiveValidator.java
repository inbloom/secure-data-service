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
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 
 */
@Component
public class TeacherToStudentCompetencyObjectiveValidator extends AbstractContextValidator {
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    EdOrgHelper helper;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.STUDENT_COMPETENCY_OBJECTIVE.equals(entityType);
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, entityType, ids)) {
            return false;
        }
        
        Set<String> myEdOrgIds = helper.getDirectEdorgs(SecurityUtil.getSLIPrincipal().getEntity());
        
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        query.addCriteria(new NeutralCriteria(ParameterConstants.EDUCATION_ORGANIZATION_ID, NeutralCriteria.CRITERIA_IN, myEdOrgIds));
        long count = repo.count(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, query);
        return count == ids.size();
    }
    
}
