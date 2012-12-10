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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validates the context of a teacher to see the requested set of student entities.
 * Returns true if the teacher member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class TeacherToStudentValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return EntityNames.STUDENT.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.TEACHER);
    }

    @Override
    public boolean validate(String entityName, Set<String> ids) {
        boolean withSections = validatedWithSections(ids);
        boolean withCohorts = validatedWithCohorts(ids);
        boolean withPrograms = validatedWithPrograms(ids);
        return withSections || withCohorts || withPrograms;
    }
    
    private Set<String> getTeachersSchools() {
        HashSet<String> toRet = new HashSet<String>();
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.TEACHER_ID,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        basicQuery.setIncludeFields(Arrays.asList(ParameterConstants.SCHOOL_ID));
        Iterable<Entity> schools = repo.findAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, basicQuery);
        for (Entity school : schools) {
            String schoolId = (String) school.getBody().get(ParameterConstants.SCHOOL_ID);
            toRet.add(schoolId);
        }
        return toRet;
    }

    private boolean validatedWithPrograms(Set<String> ids) {
        boolean match = false;

        Set<String> teacherSchoolIds = getTeachersSchools();
        Set<String> staffProgramIds = new HashSet<String>();
        Set<String> studentProgramIds = new HashSet<String>();
        Set<String> enabledPrograms = new HashSet<String>();
        // Find my current association
        for (String schoolId : teacherSchoolIds) {

            NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL,
                    schoolId));
            Iterable<Entity> edorgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, basicQuery);
            for (Entity edorg : edorgs) {
                if (edorg.getBody().containsKey(ParameterConstants.PROGRAM_REFERENCE)) {
                    List<String> programReferences = (List<String>) edorg.getBody().get(
                            ParameterConstants.PROGRAM_REFERENCE);
                    for (String programReference : programReferences) {
                        enabledPrograms.add(programReference);
                    }
                }
            }

        }
        // Get my staffProgramAssociations
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL,
                SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        addEndDateToQuery(basicQuery);
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        Iterable<Entity> staffCas = repo.findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, basicQuery);
        
        // Look at only the SCAs for programs in my edorg with date/record access
        for (Entity sca : staffCas) {
            String programId = (String) sca.getBody().get(ParameterConstants.PROGRAM_ID);
            staffProgramIds.add(programId);
        }
        
        // Get studentProgramAssociations
        for (String id : ids) {
            basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            addEndDateToQuery(basicQuery);
            Iterable<Entity> studentPrograms = repo.findAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, basicQuery);
            for (Entity studentProgram : studentPrograms) {
                studentProgramIds.add((String) studentProgram.getBody().get(ParameterConstants.PROGRAM_ID));
            }
            Set<String> tempSet = new HashSet<String>(staffProgramIds);
            tempSet.retainAll(studentProgramIds);
            if (tempSet.size() == 0) {
                return false;
            } else {
                match = true;
            }

        }
        return match;
    }


    private boolean validatedWithCohorts(Set<String> ids) {
        boolean match = false;
        Set<String> staffCohortIds = new HashSet<String>();

        // Get my staffCohortAssociations
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL,
                SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        addEndDateToQuery(basicQuery);
        Iterable<Entity> staffCas = repo.findAll(EntityNames.STAFF_COHORT_ASSOCIATION, basicQuery);
        // Look at only the SCAs for cohorts in my edorg with date/record access
        if (staffCas != null) {
            for (Entity sca : staffCas) {
                String cohortId = (String) sca.getBody().get(ParameterConstants.COHORT_ID);
                staffCohortIds.add(cohortId);
            }
        }

        Iterable<Entity> studentList =
                helper.getEntitiesWithDenormalizedReference(EntityNames.STUDENT, "cohort", new ArrayList<String>(staffCohortIds));
        Set<String> studentIds = new HashSet<String>();
        for (Entity student : studentList) {
            List<Map<String, Object>> cohortList = student.getDenormalizedData().get("cohort");
            for (Map<String, Object> cohort : cohortList) {
                String cohortRefId = (String) cohort.get("_id");
                if (staffCohortIds.contains(cohortRefId)) {
                    if (!isFieldExpired(cohort, ParameterConstants.END_DATE)) {
                        studentIds.add(student.getEntityId());
                        break;
                    }
                }
            }
        }
        match = studentIds.containsAll(ids);
        return match;
    }

    private boolean validatedWithSections(Set<String> ids) {
        Set<String> teacherSections = new HashSet<String>();
        boolean match = false;


        NeutralQuery basicQuery = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.TEACHER_ID, NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                        .getSLIPrincipal().getEntity().getEntityId()));
        addEndDateToQuery(basicQuery);

        Iterable<Entity> tsas = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, basicQuery);
        if (tsas != null) {
            for (Entity tsa : tsas) {
                teacherSections.add((String) tsa.getBody().get(ParameterConstants.SECTION_ID));
            }
        }

        for (String id : ids) {
            Set<String> studentSections = new HashSet<String>();
            basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            addEndDateToQuery(basicQuery);
            Iterable<Entity> ssas = repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, basicQuery);
            if (ssas != null) {
                for (Entity ssa : ssas) {
                    studentSections.add((String) ssa.getBody().get(ParameterConstants.SECTION_ID));
                }
            }

            Set<String> tempSet = new HashSet<String>(teacherSections);
            tempSet.retainAll(studentSections);
            if (tempSet.size() == 0) {
                return false;
            } else {
                match = true;
            }
        }
        return match;
    }


    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
