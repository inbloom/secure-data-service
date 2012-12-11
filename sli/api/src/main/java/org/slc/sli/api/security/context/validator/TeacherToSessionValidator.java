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

import java.util.Collection;
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
 * Validates the sessions you can directly see by looking at the sections you
 * can see.
 * 
 */
@Component
public class TeacherToSessionValidator extends AbstractContextValidator {
    
    @Autowired
    private TeacherToSectionValidator sectionValidator;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.SESSION.equals(entityType) && !isTransitive;
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.SESSION, entityType, ids)) {
            return false;
        }
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.SESSION_ID,
                NeutralCriteria.CRITERIA_IN, ids));
        return sectionValidator.validate(EntityNames.SECTION,
                new HashSet<String>((Collection<? extends String>) repo.findAllIds(EntityNames.SECTION, basicQuery)));
    }
    
    /**
     * @param sectionValidator
     *            the sectionValidator to set
     */
    public void setSectionValidator(TeacherToSectionValidator sectionValidator) {
        this.sectionValidator = sectionValidator;
    }
    
}
