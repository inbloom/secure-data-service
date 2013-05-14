/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.bulk.extract.context.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.impl.CohortContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.CourseOfferingContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.EducationOrganizationContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.GradebookEntryContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.GradingPeriodContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.ParentContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.SectionContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.SessionContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StaffTeacherContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StaffTeacherDirectRelatedContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StudentContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StudentDirectRelatedContextResolver;
import org.slc.sli.common.constants.EntityNames;

/**
 * Factory class for context resolvers, which are used to
 * enforce business visibility rule as in determines which
 * LEAs own the entity
 * 
 * @author ycao
 * 
 */
@Component
public class EdOrgContextResolverFactory {
    private static final Logger LOG = LoggerFactory.getLogger(EdOrgContextResolverFactory.class);
      
    @Autowired
    private EducationOrganizationContextResolver edOrgContextResolver;
    
    /**
     * Two things must in common for a entity to be a student direct related entity:
     * 1. the entity must have a "body.studentId" field
     * 2. the business rule for visibility for those entities must be: all for all current students,
     * i.e. we only check if the student belongs to a certain LEA
     */
    @Autowired
    private StudentDirectRelatedContextResolver studentDirectRelatedContextResolver;
    
    @Autowired
    private StudentContextResolver studentResolver;
    
    @Autowired
    private ParentContextResolver parentResolver;
    
    @Autowired
    private SectionContextResolver sectionResolver;
    
    @Autowired
    private StaffTeacherContextResolver staffTeacherResolver;
    
    @Autowired
    private StaffTeacherDirectRelatedContextResolver staffTeacherRelatedResolver;

    @Autowired
    private GradebookEntryContextResolver gradebookEntryContextResolver;

    @Autowired
    private CohortContextResolver cohortResolver;
    
    @Autowired
    private SessionContextResolver sessionResolver;
    
    @Autowired
    private GradingPeriodContextResolver gradingPeriodResolver;
    
    @Autowired
    private CourseOfferingContextResolver courseOfferingResolver;

    /**
     * find responsible resolver for this entity type
     * 
     * @param entityType
     * @return context resolver for this entity type
     */
    public ContextResolver getResolver(String entityType) {
        
        if (EntityNames.EDUCATION_ORGANIZATION.equals(entityType)) {
            return edOrgContextResolver;
        }
        
        if (EntityNames.STUDENT.equals(entityType)) {
            return studentResolver;
        }
        
        if (EntityNames.STUDENT_SCHOOL_ASSOCIATION.equals(entityType)
                || EntityNames.STUDENT_ASSESSMENT.equals(entityType)
                || EntityNames.STUDENT_PARENT_ASSOCIATION.equals(entityType)
                || EntityNames.STUDENT_SECTION_ASSOCIATION.equals(entityType)
                || EntityNames.GRADE.equals(entityType)
                || EntityNames.REPORT_CARD.equals(entityType)
                || EntityNames.STUDENT_ACADEMIC_RECORD.equals(entityType)
                || EntityNames.STUDENT_GRADEBOOK_ENTRY.equals(entityType)
                || EntityNames.STUDENT_COHORT_ASSOCIATION.equals(entityType)
                || EntityNames.ATTENDANCE.equals(entityType)) {
            return studentDirectRelatedContextResolver;
        }
        
        if (EntityNames.PARENT.equals(entityType)) {
            return parentResolver;
        }
        
        if (EntityNames.SECTION.equals(entityType)) {
            return sectionResolver;
        }
      
        if (EntityNames.TEACHER.equals(entityType) 
                || EntityNames.STAFF.equals(entityType)) {
            return staffTeacherResolver;
        }
        
        if (EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(entityType)
                || EntityNames.TEACHER_SECTION_ASSOCIATION.equals(entityType)
                || EntityNames.STAFF_ED_ORG_ASSOCIATION.equals(entityType)
                || EntityNames.STAFF_COHORT_ASSOCIATION.equals(entityType)) {
            return staffTeacherRelatedResolver;
        }

        if (EntityNames.GRADEBOOK_ENTRY.equals(entityType)) {
            return gradebookEntryContextResolver;
        }
        
        if (EntityNames.COHORT.equals(entityType)) {
            return cohortResolver;
        }
        
        if (EntityNames.SESSION.equals(entityType)) {
            return sessionResolver;
        }

        if (EntityNames.GRADING_PERIOD.equals(entityType)) {
            return gradingPeriodResolver;
        }
        
        if (EntityNames.COURSE_OFFERING.equals(entityType)) {
            return courseOfferingResolver;
        }

        LOG.debug("unable to resolve entity type {}", entityType);
        return null;
    }
}
