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
 * Validates the sessions you can directly see by looking at the edorgs you
 * are associated with and their lineage.
 * 
 */
@Component
public class TeacherToSessionValidator extends AbstractContextValidator {
    
    @Autowired PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.SESSION.equals(entityType) && !isTransitive;
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.SESSION, entityType, ids)) {
            return false;
        }

        return getValid(entityType, ids).containsAll(ids);
    }

    @Override
    public Set<String> getValid(String entityType, Set<String> ids) {
        Set<String> validSessions = new HashSet<String>();

        {
            Set<String> edOrgs = getTeacherEdorgLineage();

            Iterable<Entity> sessions = repo.findAll(EntityNames.SESSION,
                    new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids)));

            for( Entity session : sessions) {
                if (edOrgs.contains(session.getBody().get(ParameterConstants.SCHOOL_ID))) {
                    validSessions.add(session.getEntityId());
                }
            }

        }

        return validSessions;
    }


    
}
