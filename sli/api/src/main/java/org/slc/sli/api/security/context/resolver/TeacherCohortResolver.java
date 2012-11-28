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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Resolves teacher's access and security context to cohort records.
 * Uses staffCohortAssociation and filters on the association end date.
 */
@Component
public class TeacherCohortResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentSectionAssociationEndDateFilter dateFilter;
    
    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    private TeacherStudentResolver studentResolver;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COHORT.equals(toEntityType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds;
        if (!securityCachingStrategy.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT));
        }
        // teacher -> staffCohortAssociation
        Iterable<Entity> studentCohortAssociations = helper.getReferenceEntities(
                EntityNames.STUDENT_COHORT_ASSOCIATION,
                ParameterConstants.STUDENT_ID, studentIds);

        // filter on end_date to get list of cohortIds
        List<String> cohortIds = new ArrayList<String>();
        final String currentDate = dateFilter.getCurrentDate();
        for (Entity assoc : studentCohortAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                cohortIds.add((String) assoc.getBody().get(ParameterConstants.COHORT_ID));
            }
        }
        Iterable<Entity> staffCohort = helper.getReferenceEntities(EntityNames.STAFF_COHORT_ASSOCIATION,
                ParameterConstants.STAFF_ID, Arrays.asList(principal.getEntityId()));
        for (Entity assoc : staffCohort) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                cohortIds.add((String) assoc.getBody().get(ParameterConstants.COHORT_ID));
            }
        }
        
        securityCachingStrategy.warm(EntityNames.COHORT, new HashSet<String>(cohortIds));
        return cohortIds;
    }

}

