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
 * Resolves which parent(s) a given teacher is allowed to access
 */
@Component
public class TeacherParentResolver implements EntityContextResolver {
    
    @Autowired
    private TeacherToStudentParentAssociationResolver spaResolver;

    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        // return false;
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.PARENT.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> spaIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.STUDENT_PARENT_ASSOCIATION)) {
            spaIds = spaResolver.findAccessible(principal);
        } else {
            spaIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT_PARENT_ASSOCIATION));
        }
        Iterable<Entity> spas = repo.findAll(EntityNames.STUDENT_PARENT_ASSOCIATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, spaIds)));
        List<String> finalIds = new ArrayList<String>();
        for (Entity spa : spas) {
            if (spa.getBody().containsKey(ParameterConstants.PARENT_ID)) {
                finalIds.add((String) spa.getBody().get(ParameterConstants.PARENT_ID));
            }
        }
        securityCachingStrategy.warm(EntityNames.PARENT, new HashSet<String>(finalIds));
        return finalIds;
    }
}
