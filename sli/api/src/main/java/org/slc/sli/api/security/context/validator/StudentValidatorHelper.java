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

package org.slc.sli.api.security.context.validator;

import static org.slc.sli.api.constants.ParameterConstants.STUDENT_RECORD_ACCESS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentValidatorHelper {

    @Autowired
    private DateHelper dateHelper;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    public List<String> getStudentIds() {
        Entity principal = SecurityUtil.getSLIPrincipal().getEntity();
        Set<String> ids = new TreeSet<String>();

        ids.addAll(findAccessibleThroughSection(principal));
        ids.addAll(findAccessibleThroughCohort(principal));
        ids.addAll(findAccessibleThroughProgram(principal));

        return new ArrayList<String>(ids);
    }
    public List<String> getTeachersSectionIds(Entity teacher) {
        List<String> sectionIds = new ArrayList<String>();

        // teacher -> teacherSectionAssociation
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.TEACHER_ID,
                NeutralCriteria.OPERATOR_EQUAL, teacher.getEntityId()));
        Iterable<Entity> teacherSectionAssociations = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, query);

        for (Entity assoc : teacherSectionAssociations) {
            if (!dateHelper.isFieldExpired(assoc.getBody(), ParameterConstants.END_DATE, true)) {
                sectionIds.add((String) assoc.getBody().get(ParameterConstants.SECTION_ID));
            }
        }
        return sectionIds;
    }

    private List<String> findAccessibleThroughSection(Entity principal) {

        List<String> sectionIds = getTeachersSectionIds(principal);

        NeutralQuery query = new NeutralQuery();
        query.setLimit(0);
        query.setOffset(0);
        query.addCriteria(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, sectionIds));
        query.setEmbeddedFields(Arrays.asList(EntityNames.STUDENT_SECTION_ASSOCIATION));

        Iterable<Entity> sections = repo.findAll(EntityNames.SECTION, query);

        List<Entity> studentSectionAssociations = new ArrayList<Entity>();
        for (Entity section : sections) {
            Map<String, List<Entity>> embeddedData = section.getEmbeddedData();

            if (embeddedData != null) {
                List<Entity> associations = embeddedData.get(EntityNames.STUDENT_SECTION_ASSOCIATION);
                if (associations != null) {
                    studentSectionAssociations.addAll(associations);
                }
            }
        }

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentSectionAssociations) {
            if (!dateHelper.isFieldExpired(assoc.getBody(), ParameterConstants.END_DATE, true)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(sectionIds);

        return returnIds;
    }

    private List<String> findAccessibleThroughProgram(Entity principal) {

        // teacher -> staffProgramAssociation
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID,
                NeutralCriteria.OPERATOR_EQUAL, principal.getEntityId()));
        Iterable<Entity> staffProgramAssociations = repo.findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, query);

        // filter on end_date to get list of programIds
        List<String> programIds = new ArrayList<String>();
        for (Entity assoc : staffProgramAssociations) {
            Object studentRecordAccess = assoc.getBody().get(STUDENT_RECORD_ACCESS);
            if (studentRecordAccess != null && (Boolean) studentRecordAccess) {
                if (!dateHelper.isFieldExpired(assoc.getBody(), ParameterConstants.END_DATE, false)) {
                    programIds.add((String) assoc.getBody().get(ParameterConstants.PROGRAM_ID));
                }
            }
        }

        // program -> studentProgramAssociation
        query = new NeutralQuery(new NeutralCriteria(ParameterConstants.PROGRAM_ID, NeutralCriteria.CRITERIA_IN,
                programIds));
        Iterable<Entity> studentProgramAssociations = repo.findAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, query);

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentProgramAssociations) {
            if (!dateHelper.isFieldExpired(assoc.getBody(), ParameterConstants.END_DATE, false)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(programIds);

        return returnIds;
    }

    private List<String> findAccessibleThroughCohort(Entity principal) {

        // teacher -> staffCohortAssociation
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID,
                NeutralCriteria.OPERATOR_EQUAL, principal.getEntityId()));
        Iterable<Entity> staffCohortAssociations = repo.findAll(EntityNames.STAFF_COHORT_ASSOCIATION, query);

        List<String> cohortIds = new ArrayList<String>();
        for (Entity assoc : staffCohortAssociations) {
            Object studentRecordAccess = assoc.getBody().get(STUDENT_RECORD_ACCESS);
            if (studentRecordAccess != null && (Boolean) assoc.getBody().get(STUDENT_RECORD_ACCESS)) {
                if (!dateHelper.isFieldExpired(assoc.getBody(), ParameterConstants.END_DATE, false)) {
                    cohortIds.add((String) assoc.getBody().get(ParameterConstants.COHORT_ID));
                }
            }
        }

        // cohort -> studentCohortAssociation
        query = new NeutralQuery(new NeutralCriteria(ParameterConstants.COHORT_ID, NeutralCriteria.CRITERIA_IN,
                cohortIds));
        Iterable<Entity> studentList = repo.findAll(EntityNames.STUDENT_COHORT_ASSOCIATION, query);
        Set<String> studentIds = new HashSet<String>();

        // filter on end_date to get list of students
        for (Entity student : studentList) {
            if (!dateHelper.isFieldExpired(student.getBody(), ParameterConstants.END_DATE, false)) {
                studentIds.add((String) student.getBody().get(ParameterConstants.STUDENT_ID));
            }

        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(cohortIds);

        return returnIds;
    }
}
