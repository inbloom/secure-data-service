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
import java.util.List;
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
 * Validates access to sections that you are both directly associated to and that you have
 * access THROUGH your students to.
 * 
 */
@Component
public class TransitiveTeacherToSectionValidator extends AbstractContextValidator {
    @Autowired
    private TeacherToSectionValidator sectionValidator;
    
    @Autowired
    private TeacherToStudentValidator studentValidator;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.SECTION.equals(entityType) && isTransitive;
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.SECTION, entityType, ids)) {
            return false;
        }
        Set<String> studentIds = new HashSet<String>();
        for (String id : ids) {
            NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.SECTION_ID, NeutralCriteria.OPERATOR_EQUAL, id));
            List<Entity> ssas = (List) repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, basicQuery);
            boolean studentValid = false;
            for(Entity ssa : ssas) {
                if(isFieldExpired(ssa.getBody(), ParameterConstants.END_DATE)) {
                   continue 
                }
            }
        }
        return false;
    }
    
    /**
     * @param sectionValidator
     *            the sectionValidator to set
     */
    public void setSectionValidator(TeacherToSectionValidator sectionValidator) {
        this.sectionValidator = sectionValidator;
    }
    
    /**
     * @param studentValidator
     *            the studentValidator to set
     */
    public void setStudentValidator(TeacherToStudentValidator studentValidator) {
        this.studentValidator = studentValidator;
    }
    
}
