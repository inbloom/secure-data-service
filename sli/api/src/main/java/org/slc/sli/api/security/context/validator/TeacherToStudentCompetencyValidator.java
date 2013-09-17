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

import java.util.*;

import org.slc.sli.api.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Note: Almost identical to grade validator
 */
@Component
public class TeacherToStudentCompetencyValidator extends AbstractContextValidator {

    @Autowired
    TeacherToSubStudentEntityValidator sectionAssocValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.STUDENT_COMPETENCY.equals(entityType);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_COMPETENCY, entityType, ids)) {
            return Collections.EMPTY_SET;
        }

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        query.setIncludeFields(Arrays.asList(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID));
        Iterable<Entity> comps = getRepo().findAll(EntityNames.STUDENT_COMPETENCY, query);
        Map<String, Set<String>> secAssocIds = new HashMap<String, Set<String>>();
        for (Entity comp : comps) {
            String id = (String) comp.getBody().get(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);
            if (!secAssocIds.containsKey(id)) {
                secAssocIds.put(id, new HashSet<String>());
            }
            secAssocIds.get(id).add(comp.getEntityId());
        }

        Set<String> validSectionIds = sectionAssocValidator.validate(EntityNames.STUDENT_SECTION_ASSOCIATION, secAssocIds.keySet());

        return getValidIds(validSectionIds, secAssocIds);
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.TEACHER_CONTEXT;
    }

}
