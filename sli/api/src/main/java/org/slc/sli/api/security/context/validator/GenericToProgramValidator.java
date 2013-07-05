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

import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Decides if given user has access to given program
 * 
 * This validator is used for both staff and teachers for access to student and staff through a program
 */
@Component
public class GenericToProgramValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isStudentOrParent() && EntityNames.PROGRAM.equals(entityType) && !isTransitive;
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.PROGRAM, entityType, ids)) {
            return false;
        }
        
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("body.staffId", "=", SecurityUtil.getSLIPrincipal().getEntity().getEntityId(), false));

        if (SecurityUtil.getSLIPrincipal().isStudentAccessFlag()) {
            nq.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS, NeutralCriteria.OPERATOR_EQUAL, true));
        }

        // Fetch associations
        addEndDateToQuery(nq, false);

        Set<String> programsToValidate = new HashSet<String>(ids);
        Iterable<Entity> assocs = getRepo().findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, nq);
        for (Entity assoc : assocs) {
            programsToValidate.remove((String) assoc.getBody().get("programId"));
        }

        return programsToValidate.isEmpty();
    }
}
