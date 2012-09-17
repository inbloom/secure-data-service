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
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Resolves which StudentDisciplineIncidentAssociation a given teacher is allowed to see.
 */
@Component
public class TeacherToStudentDisciplineIncidentAssociationResolver implements EntityContextResolver {

    @Autowired
    private TeacherStudentResolver studentResolver;
    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
//        return false;
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT));
        }
        Iterable<String> sdiaIds = repo.findAllIds(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,
                new NeutralQuery(new NeutralCriteria("studentId", NeutralCriteria.CRITERIA_IN, studentIds)));
        List<String> finalIds = new ArrayList<String>();
        for (String id : sdiaIds) {
            finalIds.add(id);
        }
        securityCachingStrategy.warm(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new HashSet<String>(finalIds));
        return finalIds;
    }


}
