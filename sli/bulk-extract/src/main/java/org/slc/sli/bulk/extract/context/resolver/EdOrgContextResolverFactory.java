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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.impl.CohortContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.CourseContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.CourseOfferingContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.EducationOrganizationContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.GradebookEntryContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.GradingPeriodContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.GraduationPlanContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.ParentContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.ProgramContextResolver;
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
    
    @Autowired
    private CourseContextResolver courseResolver;

    @Autowired
    private ProgramContextResolver programContextResolver;
    
    @Autowired
    private GraduationPlanContextResolver graduationPlanResolver;
    
    private Map<String, ContextResolver> resolverMap = new HashMap<String, ContextResolver>();

    @PostConstruct
    void init() {
        
        resolverMap.put(EntityNames.EDUCATION_ORGANIZATION, edOrgContextResolver);

        resolverMap.put(EntityNames.STUDENT, studentResolver);

        resolverMap.put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.STUDENT_ASSESSMENT, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.STUDENT_PARENT_ASSOCIATION, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.STUDENT_SECTION_ASSOCIATION, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.GRADE, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.REPORT_CARD, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.STUDENT_ACADEMIC_RECORD, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.STUDENT_GRADEBOOK_ENTRY, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.STUDENT_COHORT_ASSOCIATION, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.ATTENDANCE, studentDirectRelatedContextResolver);
        resolverMap.put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, studentDirectRelatedContextResolver);
        
        resolverMap.put(EntityNames.PARENT, parentResolver);
        
        resolverMap.put(EntityNames.SECTION, sectionResolver);
        
        resolverMap.put(EntityNames.TEACHER, staffTeacherResolver);
        resolverMap.put(EntityNames.STAFF, staffTeacherResolver);
        
        resolverMap.put(EntityNames.TEACHER_SCHOOL_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.TEACHER_SECTION_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.STAFF_ED_ORG_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.STAFF_COHORT_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.STAFF_PROGRAM_ASSOCIATION, staffTeacherRelatedResolver);
        
        resolverMap.put(EntityNames.GRADEBOOK_ENTRY, gradebookEntryContextResolver);
        
        resolverMap.put(EntityNames.COHORT, cohortResolver);
        
        resolverMap.put(EntityNames.SESSION, sessionResolver);

        resolverMap.put(EntityNames.GRADING_PERIOD, gradingPeriodResolver);
        
        resolverMap.put(EntityNames.COURSE_OFFERING, courseOfferingResolver);
        
        resolverMap.put(EntityNames.COURSE, courseResolver);
        
        resolverMap.put(EntityNames.PROGRAM, programContextResolver);
        
        resolverMap.put(EntityNames.GRADUATION_PLAN, graduationPlanResolver);
    }
    
    /**
     * find responsible resolver for this entity type
     * 
     * @param entityType
     * @return context resolver for this entity type
     */
    public ContextResolver getResolver(String entityType) {
        return resolverMap.get(entityType);
    }
}
