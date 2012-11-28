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
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Resolves which teachers a given teacher is allowed to see
 *
 * @author dkornishev
 *
 */
@Component
public class TeacherCourseResolver implements EntityContextResolver {

    @Autowired
    private TeacherCourseOfferingResolver coResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;

    @Autowired
    
    private PagingRepositoryDelegate<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COURSE.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> courseOfferingIds;
        if (!securityCache.contains(EntityNames.COURSE_OFFERING)) {
            courseOfferingIds = coResolver.findAccessible(principal);
        } else {
            courseOfferingIds = new ArrayList<String>(securityCache.retrieve(EntityNames.COURSE_OFFERING));
        }
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("_id", "in", courseOfferingIds));

        Iterable<Entity> entities = repository.findAll(EntityNames.COURSE_OFFERING, neutralQuery);
        Set<String> courseIds = new HashSet<String>();

        for (Entity e : entities) {
            String courseId = (String) e.getBody().get("courseId");
            if (courseId != null) {
                courseIds.add(courseId);
            }
        }
        securityCache.warm(EntityNames.COURSE, courseIds);
        return new ArrayList<String>(courseIds);
    }

}
