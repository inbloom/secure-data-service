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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: dkornishev
 */
@Component
public class TransitiveStudentToStudentValidator extends BasicValidator {
    
    @Autowired
    private DateHelper dateHelper;
    
    public TransitiveStudentToStudentValidator() {
        super(true, EntityNames.STUDENT, EntityNames.STUDENT);
    }
    
    @Override
    protected boolean doValidate(Set<String> ids, Entity authenticatedStudent, String entityType) {
        if (!areParametersValid(EntityNames.STUDENT, entityType, ids)) {
            return false;
        }
        
        ids.remove(authenticatedStudent.getEntityId());
        if (ids.size() == 0) {
            return true;
        }
        
        Set<String> allowedStudentIds = new HashSet<String>();
        
        // check for current students in my sections, programs, and cohorts. Section first.
        Set<String> currentSections = new HashSet<String>();
        List<Map<String, Object>> putativeSections = authenticatedStudent.getDenormalizedData().get("section");
        if (putativeSections != null) {
            for (Map<String, Object> section : putativeSections) {
                if (!dateHelper.isFieldExpired(section)) {
                    currentSections.add((String) section.get("_id"));
                }
            }
            NeutralQuery sectionQuery = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN,
                    currentSections, false));
            sectionQuery.setEmbeddedFields(Arrays.asList("studentSectionAssociation"));
            for (Entity section : getRepo().findAll("section", sectionQuery)) {
                List<Entity> ssas = section.getEmbeddedData().get("studentSectionAssociation");
                if (ssas != null) {
                    for (Entity ssa : ssas) {
                        if (!dateHelper.isFieldExpired(ssa.getBody())) {
                            allowedStudentIds.add((String) ssa.getBody().get("studentId"));
                        }
                    }
                }
            }
            ids.removeAll(allowedStudentIds);
            if (ids.size() == 0) {
                return true;
            }
        }
        
        // now for programs
        Set<String> authenticatedStudentPrograms = findMyAllowedEntities(authenticatedStudent, "studentProgramAssociation", "programId");
        if (authenticatedStudentPrograms.size() > 0) {
            Set<String> studentsInPrograms = findAllowedStudentsByCurrentCohort(ids, authenticatedStudentPrograms, "studentProgramAssociation", "programId");
            ids.removeAll(studentsInPrograms);
            if (ids.size() == 0) {
                return true;
            }
        }
        
        // now for cohorts
        Set<String> authenticatedStudentCohorts = findMyAllowedEntities(authenticatedStudent, "studentCohortAssociation", "cohortId");
        if (authenticatedStudentCohorts.size() > 0) {
            Set<String> studentsInCohorts = findAllowedStudentsByCurrentCohort(ids, authenticatedStudentCohorts, "studentCohortAssociation", "cohortId");
            ids.removeAll(studentsInCohorts);
            if (ids.size() == 0) {
                return true;
            }
        }
        
        return false;
    }
    
    private Set<String> findMyAllowedEntities(Entity authenticatedStudent, String subdocType, String idField) {
        Set<String> authenticatedStudentCohorts = new HashSet<String>();
        List<Entity> cohortAssocs = authenticatedStudent.getEmbeddedData().get(subdocType);
        if (cohortAssocs != null) {
            for (Entity putativeCohort : cohortAssocs) {
                if (!dateHelper.isFieldExpired(putativeCohort.getBody())) {
                    authenticatedStudentCohorts.add((String)putativeCohort.getBody().get(idField));
                }
            }
        }
        return authenticatedStudentCohorts;
    }
    
    private Set<String> findAllowedStudentsByCurrentCohort(Set<String> studentIds, Set<String> allowedEntityIds, String subdocType, String idField) {
        Set<String> matchingStudents = new HashSet<String>();
        NeutralQuery studentQuery = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN,
                studentIds, false));
        studentQuery.setEmbeddedFields(Arrays.asList(subdocType));
        for (Entity student : getRepo().findAll("student", studentQuery)) {
            List<Entity> scas = student.getEmbeddedData().get(subdocType);
            if (scas != null) {
                for (Entity sca : scas) {
                    String cohortId = (String)sca.getBody().get(idField);
                    if (allowedEntityIds.contains(cohortId) && !dateHelper.isFieldExpired(sca.getBody())) {
                        matchingStudents.add(student.getEntityId());
                    }
                }
            }
        }
        return matchingStudents;
    }
}
