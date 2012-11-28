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
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Resolves teacher's access and security context to program records.
 * Uses staffProgramAssociation and filters on the association end date.
 */
@Component
public class TeacherProgramResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentSectionAssociationEndDateFilter dateFilter;
    
    @Autowired
    TeacherStudentResolver studentResolver;

    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.PROGRAM.equals(toEntityType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findAccessible(Entity principal) {
        //Get my students
        List<String> studentIds;
        if (!securityCachingStrategy.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT));
        }
        // teacher -> staffProgramAssociation
        Iterable<Entity> studentProgramAssociations = helper.getReferenceEntities(
                EntityNames.STUDENT_PROGRAM_ASSOCIATION, ParameterConstants.STUDENT_ID, studentIds);

        // filter on end_date to get list of programIds
        List<String> programIds = new ArrayList<String>();
        final String currentDate = dateFilter.getCurrentDate();
        for (Entity assoc : studentProgramAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                programIds.add((String) assoc.getBody().get(ParameterConstants.PROGRAM_ID));
            }
        }
        securityCachingStrategy.warm(EntityNames.PROGRAM, new HashSet<String>(programIds));

        return programIds;
    }

}
