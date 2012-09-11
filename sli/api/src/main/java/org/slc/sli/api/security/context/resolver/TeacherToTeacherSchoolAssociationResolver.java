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
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolves which TeacherSection a given teacher is allowed to see.
 */
@Component
public class TeacherToTeacherSchoolAssociationResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;
    
    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        // return false;
        return EntityNames.TEACHER.equals(fromEntityType)
                && EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        Iterable<Entity> ents = helper.getReferenceEntities(EntityNames.TEACHER_SCHOOL_ASSOCIATION, "teacherId", Arrays.asList(principal.getEntityId()));
        HashSet<String> ids = new HashSet<String>();
        for (Entity ent : ents) {
            String schoolId = (String) ent.getBody().get("schoolId");
            ids.addAll(helper.findEntitiesContainingReference(EntityNames.TEACHER_SCHOOL_ASSOCIATION, "schoolId", Arrays.asList(schoolId)));
        }

        securityCachingStrategy.warm(EntityNames.TEACHER_SCHOOL_ASSOCIATION, ids);
        return new ArrayList<String>(ids);
    }

}
