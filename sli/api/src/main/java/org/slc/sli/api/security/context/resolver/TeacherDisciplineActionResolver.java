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
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Resolves which DisciplineAction a given teacher is allowed to see
 *
 * @author syau
 *
 */
@Component
public class TeacherDisciplineActionResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    private TeacherStudentResolver studentResolver;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.DISCIPLINE_ACTION.equals(toEntityType);
    }
    
    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds;
        if (!securityCache.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT));
        }
        List<String> ids = helper.findEntitiesContainingReference(EntityNames.DISCIPLINE_ACTION, "studentId",
                studentIds);
        securityCachingStrategy.warm(EntityNames.DISCIPLINE_ACTION, new HashSet<String>(ids));
        return ids;
    }
}
