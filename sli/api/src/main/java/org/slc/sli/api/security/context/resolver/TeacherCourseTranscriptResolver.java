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
import java.util.Set;

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
 * Resolves which course transcripts any given teacher can access.
 * 
 * @author kmyers
 *
 */
@Component
public class TeacherCourseTranscriptResolver implements EntityContextResolver {
    @Autowired
    private TeacherStudentAcademicRecordResolver academicResolver;
    
    @Autowired
    private TeacherStudentResolver studentResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType)
                && EntityNames.COURSE_TRANSCRIPT.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        Set<String> ids = getAcademicRecordTranscripts(principal);
        ids.addAll(getStudentTranscripts(principal));
        securityCache.warm(EntityNames.COURSE_TRANSCRIPT, ids);
        return new ArrayList<String>(ids);
    }
    
    private Set<String> getAcademicRecordTranscripts(Entity principal) {
        List<String> studentIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT_ACADEMIC_RECORD)) {
            studentIds = academicResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT_ACADEMIC_RECORD));
        }
        Iterable<String> staIds = repo.findAllIds(EntityNames.COURSE_TRANSCRIPT, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID, NeutralCriteria.CRITERIA_IN,
                        studentIds)));
        Set<String> ids = new HashSet<String>();
        for (String id : staIds) {
            ids.add(id);
        }
        return ids;
    }
    
    private Set<String> getStudentTranscripts(Entity principal) {
        
        List<String> studentIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT)) {
            studentIds = academicResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT));
        }
        Iterable<String> staIds = repo.findAllIds(EntityNames.COURSE_TRANSCRIPT, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.CRITERIA_IN, studentIds)));
        Set<String> ids = new HashSet<String>();
        for (String id : staIds) {
            ids.add(id);
        }
        return ids;
    }
}
