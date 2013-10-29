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

import org.slc.sli.bulk.extract.context.resolver.impl.CourseTranscriptContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.DisciplineActionContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.DisciplineIncidentContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.GradebookEntryContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.ParentContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.SimpleEntityTypeContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StaffTeacherContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StaffTeacherDirectRelatedContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StudentCompetencyContextResolver;
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
    private StaffTeacherContextResolver staffTeacherResolver;

    @Autowired
    private StaffTeacherDirectRelatedContextResolver staffTeacherRelatedResolver;

    @Autowired
    private GradebookEntryContextResolver gradebookEntryContextResolver;

    @Autowired
    private DisciplineIncidentContextResolver disciplineIncidentResolver;

    @Autowired
    private DisciplineActionContextResolver disciplineActionResolver;

    @Autowired
    private StudentCompetencyContextResolver studentCompetencyResolver;

    @Autowired
    private CourseTranscriptContextResolver courseTranscriptResolver;

    @Autowired
    private SimpleEntityTypeContextResolver simpleEntityTypeContextResolver;

    private Map<String, ContextResolver> resolverMap = new HashMap<String, ContextResolver>();

    @PostConstruct
    void init() {

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
        resolverMap.put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, studentDirectRelatedContextResolver);

        resolverMap.put(EntityNames.PARENT, parentResolver);
        resolverMap.put(EntityNames.GRADEBOOK_ENTRY, gradebookEntryContextResolver);
        resolverMap.put(EntityNames.COURSE_TRANSCRIPT, courseTranscriptResolver);
        resolverMap.put(EntityNames.DISCIPLINE_INCIDENT, disciplineIncidentResolver);
        resolverMap.put(EntityNames.DISCIPLINE_ACTION, disciplineActionResolver);
        resolverMap.put(EntityNames.STUDENT_COMPETENCY, studentCompetencyResolver);

        resolverMap.put(EntityNames.TEACHER, staffTeacherResolver);
        resolverMap.put(EntityNames.STAFF, staffTeacherResolver);

        resolverMap.put(EntityNames.TEACHER_SCHOOL_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.TEACHER_SECTION_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.STAFF_ED_ORG_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.STAFF_COHORT_ASSOCIATION, staffTeacherRelatedResolver);
        resolverMap.put(EntityNames.STAFF_PROGRAM_ASSOCIATION, staffTeacherRelatedResolver);

        resolverMap.put(EntityNames.LEARNING_OBJECTIVE, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.LEARNING_STANDARD, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.COMPETENCY_LEVEL_DESCRIPTOR, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.ASSESSMENT_FAMILY, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.ASSESSMENT, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.OBJECTIVE_ASSESSMENT, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.PROGRAM, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.CALENDAR_DATE, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.COURSE, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.COURSE_OFFERING, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.SESSION, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.GRADING_PERIOD, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.GRADUATION_PLAN, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.EDUCATION_ORGANIZATION, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.SECTION, simpleEntityTypeContextResolver);
        resolverMap.put(EntityNames.COHORT, simpleEntityTypeContextResolver);

        LOG.debug("Resolver map is {}", resolverMap);
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
