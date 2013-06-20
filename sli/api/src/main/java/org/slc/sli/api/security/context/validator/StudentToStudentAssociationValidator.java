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

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Note that this class differs from StudentToStudentSectionAssociationValidator
 * in that StudentCohortAssociation and StudentProgramAssociation are denormalized on student
 * while StudentSectionAssociation is denormalized on section
 */
@Component
public class StudentToStudentAssociationValidator extends AbstractContextValidator{

    @Autowired
    private TransitiveStudentToStudentValidator studentValidator;

    protected static final Set<String> STUDENT_ASSOCIATIONS = new HashSet<String>(Arrays.asList(
            EntityNames.STUDENT_COHORT_ASSOCIATION,
            EntityNames.STUDENT_PROGRAM_ASSOCIATION));

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudent() && STUDENT_ASSOCIATIONS.contains(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(STUDENT_ASSOCIATIONS, entityType, ids)) {
            return false;
        }

        Set<String> otherStudentIds = new HashSet<String>();
        Set<String> toValidateIds = new HashSet<String>(ids);

        // First, cross off associations that point back to yourself utilizing superdocs
        Entity self = SecurityUtil.getSLIPrincipal().getEntity();
        List<Entity> associations = self.getEmbeddedData().get(entityType);
        for(Entity assoc : associations) {
            if (ids.contains(assoc.getEntityId())) {
                // Entities denormalized onto the student should have the same ID as the student
                toValidateIds.remove(assoc.getEntityId());
            }
        }

        // Return now if all the requested IDs have been validated
        if (toValidateIds.isEmpty()) {
            return true;
        }

        // Now, find the student IDs on the other remaining requested IDs and validate them
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, toValidateIds));
        for(Entity association : repo.findAll(entityType, query)) {
            Map<String, Object>body = association.getBody();
            if (!isFieldExpired(body, ParameterConstants.END_DATE, false)) {
                otherStudentIds.add((String) body.get(ParameterConstants.STUDENT_ID));
            } else {
                // We cannot see Associations for other students if they are expired
                return false;
            }
        }

        // Validate the other students you found (you should have at least one,
        // if you don't, the student validator will return false if passed in an empty list)
        return studentValidator.validate(EntityNames.STUDENT, otherStudentIds);
    }
}
