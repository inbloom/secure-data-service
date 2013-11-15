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

package org.slc.sli.api.security.context;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import static org.slc.sli.common.constants.EntityNames.*;
import static org.slc.sli.common.constants.ParameterConstants.*;
import org.slc.sli.domain.Entity;
import static org.slc.sli.api.security.context.OwnershipArbiter.Reference.RefType.*;

/**
 * @author ablum npandey
 */
@Component
public class EdOrgOwnershipArbiter extends OwnershipArbiter {

    @Autowired
    private EdOrgHelper helper;

    @SuppressWarnings("unused")
    @PostConstruct
    public void init() {

        typeToReference.put(STUDENT,                                 new Reference(STUDENT, STUDENT_SCHOOL_ASSOCIATION,      STUDENT_ID, RIGHT_TO_LEFT));
        typeToReference.put(STUDENT_SCHOOL_ASSOCIATION,              new Reference(STUDENT_SCHOOL_ASSOCIATION,               EDUCATION_ORGANIZATION,SCHOOL_ID, LEFT_TO_RIGHT));
        typeToReference.put(GRADE,                                   new Reference(GRADE, STUDENT_SECTION_ASSOCIATION,       STUDENT_SECTION_ASSOCIATION_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_SECTION_ASSOCIATION,             new Reference( STUDENT_SECTION_ASSOCIATION,             STUDENT, STUDENT_ID, LEFT_TO_RIGHT));
        typeToReference.put(ATTENDANCE,                              new Reference(ATTENDANCE, SCHOOL,                       SCHOOL_ID, LEFT_TO_RIGHT));
        typeToReference.put(COHORT,                                  new Reference(COHORT,                                   EDUCATION_ORGANIZATION, "educationOrgId", LEFT_TO_RIGHT));
        typeToReference.put(COURSE_TRANSCRIPT,                       new Reference(COURSE_TRANSCRIPT,                        EDUCATION_ORGANIZATION, EDUCATION_ORGANIZATION_REFERENCE, LEFT_TO_RIGHT));
        typeToReference.put(DISCIPLINE_INCIDENT,                     new Reference(DISCIPLINE_INCIDENT,                      SCHOOL, SCHOOL_ID, LEFT_TO_RIGHT));
        typeToReference.put(DISCIPLINE_ACTION,                       new Reference(DISCIPLINE_ACTION,                        SCHOOL, "responsibilitySchoolId", LEFT_TO_RIGHT));
        typeToReference.put(GRADEBOOK_ENTRY,                         new Reference(GRADEBOOK_ENTRY,                          SECTION, SECTION_ID, LEFT_TO_RIGHT));
        typeToReference.put(GRADUATION_PLAN,                         new Reference(GRADUATION_PLAN,                          EDUCATION_ORGANIZATION, EDUCATION_ORGANIZATION_ID, LEFT_TO_RIGHT));
        typeToReference.put(CALENDAR_DATE,                           new Reference(CALENDAR_DATE,                            EDUCATION_ORGANIZATION, EDUCATION_ORGANIZATION_ID, LEFT_TO_RIGHT));
        typeToReference.put(PARENT,                                  new Reference(PARENT,                                   STUDENT_PARENT_ASSOCIATION, PARENT_ID, RIGHT_TO_LEFT));
        typeToReference.put(REPORT_CARD,                             new Reference(REPORT_CARD,                              STUDENT, STUDENT_ID, LEFT_TO_RIGHT));
        typeToReference.put(SECTION,                                 new Reference(SECTION,                                  SCHOOL, SCHOOL_ID, LEFT_TO_RIGHT));
        typeToReference.put(STAFF,                                   new Reference(STAFF,                                    STAFF_ED_ORG_ASSOCIATION, STAFF_REFERENCE, RIGHT_TO_LEFT));
        typeToReference.put(STAFF_ED_ORG_ASSOCIATION,                new Reference(STAFF_ED_ORG_ASSOCIATION,                 EDUCATION_ORGANIZATION, EDUCATION_ORGANIZATION_REFERENCE, LEFT_TO_RIGHT));
        typeToReference.put(STAFF_COHORT_ASSOCIATION,                new Reference(STAFF_COHORT_ASSOCIATION,                 STAFF, STAFF_ID, LEFT_TO_RIGHT));
        typeToReference.put(STAFF_PROGRAM_ASSOCIATION,               new Reference(STAFF_PROGRAM_ASSOCIATION,                STAFF, STAFF_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_ACADEMIC_RECORD,                 new Reference(STUDENT_ACADEMIC_RECORD,                  STUDENT, STUDENT_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_ASSESSMENT,                      new Reference(STUDENT_ASSESSMENT, STUDENT,              STUDENT_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_COHORT_ASSOCIATION,              new Reference(STUDENT_COHORT_ASSOCIATION,               STUDENT, STUDENT_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_COMPETENCY,                      new Reference(STUDENT_COMPETENCY,                       STUDENT_SECTION_ASSOCIATION, STUDENT_SECTION_ASSOCIATION_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_COMPETENCY_OBJECTIVE,            new Reference(STUDENT_COMPETENCY_OBJECTIVE,             EDUCATION_ORGANIZATION, EDUCATION_ORGANIZATION_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new Reference(STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,  STUDENT, STUDENT_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_PARENT_ASSOCIATION,              new Reference(STUDENT_PARENT_ASSOCIATION, STUDENT,      STUDENT_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_GRADEBOOK_ENTRY,                 new Reference(STUDENT_GRADEBOOK_ENTRY, GRADEBOOK_ENTRY, GRADEBOOK_ENTRY_ID, LEFT_TO_RIGHT));
        typeToReference.put(STUDENT_PROGRAM_ASSOCIATION,             new Reference(STUDENT_PROGRAM_ASSOCIATION,              EDUCATION_ORGANIZATION, EDUCATION_ORGANIZATION_ID, LEFT_TO_RIGHT));
        typeToReference.put(TEACHER_SECTION_ASSOCIATION,             new Reference(TEACHER_SECTION_ASSOCIATION, STAFF,       TEACHER_ID, LEFT_TO_RIGHT));
        typeToReference.put(TEACHER_SCHOOL_ASSOCIATION,              new Reference(TEACHER_SCHOOL_ASSOCIATION, SCHOOL,       SCHOOL_ID, LEFT_TO_RIGHT));
        typeToReference.put(TEACHER,                                 new Reference(TEACHER, STAFF_ED_ORG_ASSOCIATION,        STAFF_REFERENCE, RIGHT_TO_LEFT));
        typeToReference.put(APPLICATION,                             new Reference(APPLICATION, EDUCATION_ORGANIZATION,      AUTHORIZED_EDORGS, LEFT_TO_RIGHT));
        typeToReference.put(APPLICATION_AUTHORIZATION,               new Reference(APPLICATION_AUTHORIZATION, APPLICATION,   APPLICATION_ID, LEFT_TO_RIGHT));
    }

    /**
     * Look up the edorgs that can access the given entities.
     *
     * @param entities
     * @param entityType
     * @return a Set of edorg IDs
     */
    public Set<String> determineEdorgs(Iterable<Entity> entities, String entityType) {
        Set<String> edorgs = new HashSet<String>();

        for (Entity entity : findOwner(entities, entityType, false)) {
            edorgs.add(entity.getEntityId());
        }

        return edorgs;
    }

    /**
     * Look up the edorgs that can access the given entities.
     *
     * @param entities
     * @param entityType
     * @return a Set of edorg IDs
     */
    public Set<String> determineHierarchicalEdorgs(Iterable<Entity> entities, String entityType) {
        Set<String> hierarchicalEdorgs = new HashSet<String>();

        List<Entity> edorgs = findOwner(entities, entityType, true);

        for (Entity edorg : edorgs) {
            hierarchicalEdorgs.add(edorg.getEntityId());
            hierarchicalEdorgs.addAll(helper.getParentEdOrgs(edorg));
        }

        return hierarchicalEdorgs;
    }

    /**
     * Determines if the specified entity type represents an education organization.
     *
     * @param type
     *            Type of Entity to be checked.
     * @return True if the specified entity type is an education organization, false otherwise.
     */
    @Override
    protected boolean isBaseType(String type) {
        if (type == null) {
            return false;
        }

        return type.equals(EDUCATION_ORGANIZATION) || type.equals(SCHOOL)
                || type.equals("localEducationAgency") || type.equals("stateEducationAgency");
    }
}
