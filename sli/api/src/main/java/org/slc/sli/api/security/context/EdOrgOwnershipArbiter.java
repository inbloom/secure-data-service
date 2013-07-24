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


import javax.annotation.PostConstruct;

import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        typeToReference
                .put(EntityNames.STUDENT, new Reference(EntityNames.STUDENT, EntityNames.STUDENT_SCHOOL_ASSOCIATION,
                        ParameterConstants.STUDENT_ID, Reference.RefType.RIGHT_TO_LEFT));
        
        typeToReference.put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, new Reference(
                EntityNames.STUDENT_SCHOOL_ASSOCIATION, EntityNames.EDUCATION_ORGANIZATION,
                ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        
        typeToReference.put(EntityNames.GRADE, new Reference(EntityNames.GRADE,
                EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_SECTION_ASSOCIATION, new Reference(
                EntityNames.STUDENT_SECTION_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.ATTENDANCE, new Reference(EntityNames.ATTENDANCE, EntityNames.SCHOOL,
                ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COHORT, new Reference(EntityNames.COHORT, EntityNames.EDUCATION_ORGANIZATION,
                "educationOrgId", Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COURSE_TRANSCRIPT, new Reference(EntityNames.COURSE_TRANSCRIPT,
                EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.DISCIPLINE_INCIDENT, new Reference(EntityNames.DISCIPLINE_INCIDENT,
                EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.DISCIPLINE_ACTION, new Reference(EntityNames.DISCIPLINE_ACTION,
                EntityNames.SCHOOL, "responsibilitySchoolId", Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADEBOOK_ENTRY, new Reference(EntityNames.GRADEBOOK_ENTRY,
                EntityNames.SECTION, ParameterConstants.SECTION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADUATION_PLAN, new Reference(EntityNames.GRADUATION_PLAN,
                EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.CALENDAR_DATE, new Reference(EntityNames.CALENDAR_DATE,
                EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.PARENT, new Reference(EntityNames.PARENT,
                EntityNames.STUDENT_PARENT_ASSOCIATION, ParameterConstants.PARENT_ID, Reference.RefType.RIGHT_TO_LEFT));
        typeToReference.put(EntityNames.REPORT_CARD, new Reference(EntityNames.REPORT_CARD, EntityNames.STUDENT,
                ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.SECTION, new Reference(EntityNames.SECTION, EntityNames.SCHOOL,
                ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF, new Reference(EntityNames.STAFF, EntityNames.STAFF_ED_ORG_ASSOCIATION,
                ParameterConstants.STAFF_REFERENCE, Reference.RefType.RIGHT_TO_LEFT));
        typeToReference.put(EntityNames.STAFF_ED_ORG_ASSOCIATION, new Reference(EntityNames.STAFF_ED_ORG_ASSOCIATION,
                EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF_COHORT_ASSOCIATION, new Reference(EntityNames.STAFF_COHORT_ASSOCIATION,
                EntityNames.STAFF, ParameterConstants.STAFF_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF_PROGRAM_ASSOCIATION, new Reference(EntityNames.STAFF_PROGRAM_ASSOCIATION,
                EntityNames.STAFF, ParameterConstants.STAFF_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ACADEMIC_RECORD, new Reference(EntityNames.STUDENT_ACADEMIC_RECORD,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ASSESSMENT, new Reference(EntityNames.STUDENT_ASSESSMENT,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COHORT_ASSOCIATION, new Reference(
                EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COMPETENCY, new Reference(EntityNames.STUDENT_COMPETENCY,
                EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new Reference(
                EntityNames.STUDENT_COMPETENCY_OBJECTIVE, EntityNames.EDUCATION_ORGANIZATION,
                ParameterConstants.EDUCATION_ORGANIZATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new Reference(
                EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, EntityNames.STUDENT,
                ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_PARENT_ASSOCIATION, new Reference(
                EntityNames.STUDENT_PARENT_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_GRADEBOOK_ENTRY, new Reference(EntityNames.STUDENT_GRADEBOOK_ENTRY,
                EntityNames.GRADEBOOK_ENTRY, ParameterConstants.GRADEBOOK_ENTRY_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new Reference(
                EntityNames.STUDENT_PROGRAM_ASSOCIATION, EntityNames.EDUCATION_ORGANIZATION,
                ParameterConstants.EDUCATION_ORGANIZATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER_SECTION_ASSOCIATION, new Reference(
                EntityNames.TEACHER_SECTION_ASSOCIATION, EntityNames.STAFF, ParameterConstants.TEACHER_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new Reference(
                EntityNames.TEACHER_SCHOOL_ASSOCIATION, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER, new Reference(EntityNames.TEACHER,
                EntityNames.STAFF_ED_ORG_ASSOCIATION, ParameterConstants.STAFF_REFERENCE,
                Reference.RefType.RIGHT_TO_LEFT));
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
            hierarchicalEdorgs.addAll(helper.getHierarchicalEdOrgs(edorg));
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
        return type.equals(EntityNames.EDUCATION_ORGANIZATION) || type.equals(EntityNames.SCHOOL)
                || type.equals("localEducationAgency") || type.equals("stateEducationAgency");
    }
}
