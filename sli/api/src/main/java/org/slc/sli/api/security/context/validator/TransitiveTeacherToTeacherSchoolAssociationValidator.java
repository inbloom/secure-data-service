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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates teacher's access to given teachers
 * 
 * @author dkornishev
 * 
 */
@Component
public class TransitiveTeacherToTeacherSchoolAssociationValidator extends AbstractContextValidator {
    
    @Resource
    private TransitiveTeacherToTeacherValidator val;
    
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(entityType) && isTeacher() && isTransitive;
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.TEACHER_SCHOOL_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        Iterable<Entity> tsa = getRepo().findAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, nq);
        
        Set<String> teachers = new HashSet<String>();
        for (Entity e : tsa) {
            teachers.add((String) e.getBody().get(ParameterConstants.TEACHER_ID));
        }
        
        return val.validate(EntityNames.TEACHER, teachers);
    }
}
