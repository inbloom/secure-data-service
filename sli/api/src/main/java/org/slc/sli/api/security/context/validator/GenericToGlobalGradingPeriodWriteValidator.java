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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates Write context to a global grading period.
 */
@Component
public class GenericToGlobalGradingPeriodWriteValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && EntityNames.GRADING_PERIOD.equals(entityType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.GRADING_PERIOD, entityType, ids)) {
            return false;
        }

        /*
         * While we only only expect one ID will be passed in (as this is
         * intended for write validation) we will still be doing the logic to
         * handle a set of IDs
         *
         * Make sure that the grading periods passed in are referenced by a
         * session that is tied to your edorg hierarchy
         */
        Set<String> edOrgLineage = getEdorgDescendents(getDirectEdorgs());
        Set<String> gradingPeriodsToValidate = new HashSet<String>(ids);
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.GRADING_PERIOD_REFERENCE,
                NeutralCriteria.CRITERIA_IN, ids));
        Iterable<Entity> sessions = repo.findAll(EntityNames.SESSION, query);
        for (Entity session : sessions) {
            if (edOrgLineage.contains(session.getBody().get(ParameterConstants.SCHOOL_ID))) {
                gradingPeriodsToValidate.removeAll((Collection<String>) session.getBody().get(
                        ParameterConstants.GRADING_PERIOD_REFERENCE));
                if (gradingPeriodsToValidate.isEmpty()) {
                    // All Grading Period Ids have been validated, return success
                    return true;
                }
            }
        }
        return false;
    }

}
