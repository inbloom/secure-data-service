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
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves Teachers context to Students. Finds accessible students through section, program, and cohort associations.
 */
@Component
public class TeacherToStudentCompetencyResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;
    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    private TeacherStudentResolver studentResolver;
    
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
//        return false;
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT_COMPETENCY.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT));
        }
        Iterable<String> assocIds = repo.findAllIds(EntityNames.STUDENT_SECTION_ASSOCIATION, new NeutralQuery(new NeutralCriteria(
                "studentId", NeutralCriteria.CRITERIA_IN, studentIds)));
        List<String> ids = new ArrayList<String>();
        for (String id : assocIds) {
            ids.add(id);
        }
        List<String> finalIds = helper.findEntitiesContainingReference(EntityNames.STUDENT_COMPETENCY, "studentSectionAssociationId", ids);
        securityCachingStrategy.warm(EntityNames.STUDENT_COMPETENCY, new HashSet<String>(finalIds));
        return finalIds;
    }
}
