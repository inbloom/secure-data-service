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


package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves Teachers context to Students. Finds accessible students through section, program, and cohort associations.
 */
@Component
public class TeacherToStudentCompetencyObjectiveResolver implements EntityContextResolver {

    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    private TeacherEdOrgContextResolver edOrgResolver;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT_COMPETENCY_OBJECTIVE.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> edOrgIds;
        if (!securityCachingStrategy.contains(EntityNames.EDUCATION_ORGANIZATION)) {
            edOrgIds = edOrgResolver.findAccessible(principal);
        } else {
            edOrgIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.EDUCATION_ORGANIZATION));
        }
        
        Iterable<String> studentCompetencyObjectiveIds = repo.findAllIds(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new NeutralQuery(new NeutralCriteria("educationOrganizationId", "in", edOrgIds)));

        try {
            return (List<String>) studentCompetencyObjectiveIds;
        } catch (ClassCastException cce) {
            List<String> results = new ArrayList<String>();
            for (String studentCompetencyObjectiveId : studentCompetencyObjectiveIds) {
                results.add(studentCompetencyObjectiveId);
            }
            return results;
        }
    }
}
