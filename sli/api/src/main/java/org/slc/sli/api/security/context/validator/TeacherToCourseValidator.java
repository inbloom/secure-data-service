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

import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validate's teacher's context to course offering by looking your edorg lineage.
 * 
 * 
 */
@Component
public class TeacherToCourseValidator extends AbstractContextValidator {
    
    @Autowired
    EdOrgHelper helper;
    
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isTransitive && isTeacher() && EntityNames.COURSE.equals(entityType);
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.COURSE, entityType, ids)) {
            return false;
        }
        
        return getValid(EntityNames.COURSE, ids).size() == ids.size();
    }

    @Override
    public Set<String> getValid(String entityType, Set<String> ids) {
        
        Set<String> validIds = new HashSet<String>();
        
        Set<String> schools = getTeacherEdorgLineage();
        
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.SCHOOL_ID, NeutralCriteria.CRITERIA_IN, schools));
        Iterable<Entity> entities = getRepo().findAll(EntityNames.COURSE, nq);
        
        for (Entity entity : entities) {
            validIds.add(entity.getEntityId());
        }

        return validIds;
    }
    
}
