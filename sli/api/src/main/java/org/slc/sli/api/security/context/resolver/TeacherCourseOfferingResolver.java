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
 * Resolves which course offerings any given teacher can access.
 * 
 * @author kmyers
 *
 */
@Component
public class TeacherCourseOfferingResolver implements EntityContextResolver {
    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    private TeacherSectionResolver sectionResolver;
    
    @Autowired
    TeacherSessionResolver sessionResolver;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COURSE_OFFERING.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> sectionIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.SECTION)) {
            sectionIds = sectionResolver.findAccessible(principal);
        } else {
            sectionIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.SECTION));
        }
        
        List<String> sessionIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.SESSION)) {
            sessionIds = sessionResolver.findAccessible(principal);
        } else {
            sessionIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.SESSION));
        }
        List<String> finalIds = new ArrayList<String>();
        // Go through the sections getting out the sessionIds
        Iterable<Entity> returned = repo.findAll(EntityNames.SECTION, new NeutralQuery(new NeutralCriteria("_id",
                NeutralCriteria.OPERATOR_EQUAL, sectionIds)));
        for (Entity section : returned) {
            if (section.getBody().containsKey(ParameterConstants.COURSE_OFFERING_ID)) {
                finalIds.add((String) section.getBody().get(ParameterConstants.COURSE_OFFERING_ID));
            }
        }
        
        returned = repo.findAll(EntityNames.SESSION, new NeutralQuery(new NeutralCriteria("_id",
                NeutralCriteria.OPERATOR_EQUAL, sessionIds)));
        for (Entity section : returned) {
            if (section.getBody().containsKey(ParameterConstants.COURSE_OFFERING_ID)) {
                finalIds.add((String) section.getBody().get(ParameterConstants.COURSE_OFFERING_ID));
            }
        }


        securityCachingStrategy.warm(EntityNames.COURSE_OFFERING, new HashSet<String>(finalIds));
        return finalIds;
    }
    
}
