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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves which TeacherSection a given teacher is allowed to see.
 */
@Component
public class TeacherToStaffEdOrgAssociationResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
         return EntityNames.TEACHER.equals(fromEntityType)
                && EntityNames.STAFF_ED_ORG_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        Iterable<Entity> ents = helper.getReferenceEntities(EntityNames.TEACHER_SCHOOL_ASSOCIATION, "teacherId", Arrays.asList(principal.getEntityId()));
        HashSet<String> ids = new HashSet<String>();
        for (Entity ent : ents) {
            String schoolId = (String) ent.getBody().get("schoolId");
            for (String id : repository.findAllIds(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery(new NeutralCriteria("educationOrganizationReference", "=", schoolId)))) {
                ids.add(id);
            }
        }
        

        securityCachingStrategy.warm(EntityNames.STAFF_ED_ORG_ASSOCIATION, ids);
        return new ArrayList<String>(ids);
    }

}
