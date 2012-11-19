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
 * Resolves which grades any given teacher can access.
 * 
 * @author kmyers
 *
 */
@Component
public class TeacherGradeResolver implements EntityContextResolver {
    @Autowired
    private TeacherStudentSectionAssociationResolver studentSectionResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.GRADE.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> sectionIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT_SECTION_ASSOCIATION)) {
            sectionIds = studentSectionResolver.findAccessible(principal);
        } else {
            sectionIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT_SECTION_ASSOCIATION));
        }
        Iterable<String> gradeIds = repo.findAllIds(EntityNames.GRADE, new NeutralQuery(new NeutralCriteria(
                ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, NeutralCriteria.CRITERIA_IN, sectionIds)));
        List<String> ids = new ArrayList<String>();
        for (String id : gradeIds) {
            ids.add(id);
        }
        securityCache.warm(EntityNames.GRADE, new HashSet<String>(ids));
        return ids;
    }
}
