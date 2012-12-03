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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Resolves which teachers a given teacher is allowed to see
 *
 * @author dkornishev
 *
 */
@Component
public class TeacherSessionResolver implements EntityContextResolver {

    @Autowired
    private SessionSecurityCache securityCache;

    @Autowired
    private TeacherSectionResolver sectionResolver;

    @Autowired
    private PagingRepositoryDelegate<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.SESSION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        // Get the sections and get the sessions that they reference.
        List<String> sectionIds;
        if (!securityCache.contains(EntityNames.SECTION)) {
            sectionIds = sectionResolver.findAccessible(principal);
        } else {
            sectionIds = new ArrayList<String>(securityCache.retrieve(EntityNames.SECTION));
        }

        Set<String> sessionIds = new HashSet<String>();
        NeutralQuery neutralQuery = new NeutralQuery(0);
        neutralQuery.addCriteria(new NeutralCriteria("_id", "in", sectionIds, false));

        Iterable<Entity> entities = repository.findAll(EntityNames.SECTION, neutralQuery);

        List<String> courseOfferings = new LinkedList<String>();
        for (Entity e : entities) {
            String sessionId = (String) e.getBody().get("sessionId");
            String courseOfferingId = (String) e.getBody().get("courseOfferingId");
            if (sessionId != null) {
                sessionIds.add(sessionId);
            }

            if (courseOfferingId != null) {
                courseOfferings.add(courseOfferingId);
            }
        }

        if (!courseOfferings.isEmpty()) {
            for (String sessionId : getSessionsThroughCourseOfferings(courseOfferings)) {
                sessionIds.add(sessionId);
            }
        }

        return new ArrayList<String>(sessionIds);
    }

    /**
     * Given a list of course offering ids, finds the corresponding set of sessions that the course
     * offerings reference back to.
     *
     * @param courseOfferingIds
     *            List of UUIDs for course offerings.
     * @return List of UUIDs for sessions (referenced by course offerings).
     */
    private List<String> getSessionsThroughCourseOfferings(List<String> courseOfferingIds) {
        List<String> sessions = new LinkedList<String>();
        NeutralQuery neutralQuery = new NeutralQuery(0);
        neutralQuery.addCriteria(new NeutralCriteria("_id", "in", courseOfferingIds, false));
        Iterable<Entity> entities = repository.findAll(EntityNames.COURSE_OFFERING, neutralQuery);
        if (entities != null) {
            for (Entity entity : entities) {
                String session = (String) entity.getBody().get("sessionId");
                sessions.add(session);
            }
        }
        return sessions;
    }
}
