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

import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validate's teacher's context to course offering by looking at sections and sessions.
 * 
 * 
 */
@Component
public class TeacherToCourseOfferingValidator extends AbstractContextValidator {
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private TeacherToSectionValidator sectionValidator;
    
    @Autowired
    private TeacherToSessionValidator sessionValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.COURSE_OFFERING.equals(entityType) && !isTransitive;
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.COURSE_OFFERING, entityType, ids)) {
            return false;
        }
        Set<String> validIds = new HashSet<String>();
        // Validate each ID by either section or session
        for (String id : ids) {
            NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.COURSE_OFFERING_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            Set<String> sessionIds = (Set) repo.findAllIds(EntityNames.SESSION, basicQuery);
            
            Set<String> sectionIds = (Set) repo.findAllIds(EntityNames.SECTION, basicQuery);
            
            if (sessionValidator.validate(EntityNames.SESSION, sessionIds)
                    || sectionValidator.validate(EntityNames.SECTION, sectionIds)) {
                validIds.add(id);
            }

        }
        return ids.size() == validIds.size();
    }
    
}
