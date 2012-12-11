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

package org.slc.sli.api.security.context.validator;

import static org.slc.sli.api.constants.ParameterConstants.STUDENT_RECORD_ACCESS;

import java.util.ArrayList;
import java.util.Arrays;
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
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty()
                    || (dateFilter.isFirstDateBeforeSecondDate(getSectionGraceDate(), endDate))) {
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
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty()
                    || dateFilter.isFirstDateBeforeSecondDate(getSectionGraceDate(), endDate)) {
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
        Iterable<Entity> staffProgramAssociations = helper.getReferenceEntities(EntityNames.STAFF_PROGRAM_ASSOCIATION,
                ParameterConstants.STAFF_ID, Arrays.asList(principal.getEntityId()));
        
        // filter on end_date to get list of programIds
        List<String> programIds = new ArrayList<String>();
        final String currentDate = dateFilter.getCurrentDate();
        for (Entity assoc : staffProgramAssociations) {
            if ((Boolean) assoc.getBody().get(STUDENT_RECORD_ACCESS)) {
                String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
                if (endDate == null || endDate.isEmpty()
                        || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                    programIds.add((String) assoc.getBody().get(ParameterConstants.PROGRAM_ID));
                }
            }
        }
        
        // program -> studentProgramAssociation
        Iterable<Entity> studentProgramAssociations = helper.getReferenceEntities(
                EntityNames.STUDENT_PROGRAM_ASSOCIATION, ParameterConstants.PROGRAM_ID, programIds);
        
        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentProgramAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
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
        Iterable<Entity> staffCohortAssociations = helper.getReferenceEntities(EntityNames.STAFF_COHORT_ASSOCIATION,
                ParameterConstants.STAFF_ID, Arrays.asList(principal.getEntityId()));
        
        // filter on end_date to get list of cohortIds
        final String currentDate = dateFilter.getCurrentDate();
        List<String> cohortIds = new ArrayList<String>();
        for (Entity assoc : staffCohortAssociations) {
            if ((Boolean) assoc.getBody().get(STUDENT_RECORD_ACCESS)) {
                String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
                if (endDate == null || endDate.isEmpty()
                        || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                    cohortIds.add((String) assoc.getBody().get(ParameterConstants.COHORT_ID));
                }
            }
        }
        
        // cohort -> studentCohortAssociation
        Iterable<Entity> studentList = helper.getEntitiesWithDenormalizedReference(EntityNames.STUDENT, COHORT_REF,
                cohortIds);
        
        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity student : studentList) {
            List<Map<String, Object>> cohortList = student.getDenormalizedData().get(COHORT_REF);
            for (Map<String, Object> cohort : cohortList) {
                String endDate = (String) cohort.get(ParameterConstants.END_DATE);
                String cohortRefId = (String) cohort.get("_id");
                if ((cohortIds.contains(cohortRefId))
                        && (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate,
                                endDate))) {
                    studentIds.add(student.getEntityId());
                    break;
                }
            }
            
        }
        
        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(cohortIds);
        
        return returnIds;
    }
}
