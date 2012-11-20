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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves which DisciplineIncident a given teacher is allowed to see
 *
 * @author syau
 *
 */
@Component
public class TeacherDisciplineIncidentResolver implements EntityContextResolver {

    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private TeacherToStudentDisciplineIncidentAssociationResolver studentResolver;
    
    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.DISCIPLINE_INCIDENT.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(
                    securityCachingStrategy.retrieve(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION));
        }
        Iterable<Entity> sdias = repo.findAll(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, studentIds)));
        List<String> ids = new ArrayList<String>();
        for (Entity assoc : sdias) {
            if (assoc.getBody().containsKey(ParameterConstants.DISCIPLINE_INCIDENT_ID)) {
                ids.add((String) assoc.getBody().get(ParameterConstants.DISCIPLINE_INCIDENT_ID));
            }
        }
        securityCachingStrategy.warm(EntityNames.DISCIPLINE_INCIDENT, new HashSet<String>(ids));
        return ids;
    }
}
