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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Note that this class differs from StudentToStudentAssociationValidator
 * in that StudentCohortAssociation and StudentProgramAssociation are denormalized on student
 * while StudentSectionAssociation is denormalized on section
 */
@Component
public class TransitiveStudentToStudentSectionAssociationValidator extends AbstractContextValidator{

    @Autowired
    private TransitiveStudentToStudentValidator studentValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudentOrParent() && EntityNames.STUDENT_SECTION_ASSOCIATION.equals(entityType) && isTransitive;
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_SECTION_ASSOCIATION, entityType, ids)) {
            return Collections.emptySet();
        }

        Set<String> otherStudentIds = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        for(Entity ssa : getRepo().findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, query)) {
            Map<String, Object> body = ssa.getBody();
            if (getDirectStudentIds().contains(body.get(ParameterConstants.STUDENT_ID))) {
                // We don't have to further validate SSAs if the studentId is a reference to the user
                continue;
            }
            // At this point the STUDENT_ID is not self
            if (!isFieldExpired(body, ParameterConstants.END_DATE, false)) {
                otherStudentIds.add((String) ssa.getBody().get(ParameterConstants.STUDENT_ID));
            } else {
                // We cannot see SSAs for other students if they are expired
                return Collections.emptySet();
            }
        }

        Set<String> result = new HashSet<String>();
        if(otherStudentIds.isEmpty()) {
            result = ids;
        } else {
            result = studentValidator.validate(EntityNames.STUDENT, otherStudentIds);
        }
        return result;
    }
}
