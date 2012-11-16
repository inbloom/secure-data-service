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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

//@Component - Disable teacher validators for now
public class TeacherToStudentValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return !through && EntityNames.STUDENT.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.TEACHER);
    }

    @Override
    public Set<String> getValid(String entityType, Set<String> ids) {
        Set<String> validated = new HashSet<String>();
        validated.addAll(validatedWithSections(ids, false));
        validated.addAll(validatedWithCohorts(ids, false));
        validated.addAll(validatedWithPrograms(ids, false));
        return validated;
    }

    @Override
    public boolean validate(String entityName, Set<String> ids) {
        int length = ids.size();
        return validatedWithSections(ids, true).size() == length ||
               validatedWithCohorts(ids, true).size() == length ||
               validatedWithPrograms(ids, true).size() == length;
    }

    private Collection<String> validatedWithPrograms(Set<String> ids, boolean quitOnFirstInvalid) {
        // Get my edorg association
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.TEACHER_ID,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        Iterable<Entity> schools = repo.findAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, basicQuery);
        Set<String> teacherSchoolIds = new HashSet<String>();
        Set<String> staffProgramIds = new HashSet<String>();
        Set<String> studentProgramIds = new HashSet<String>();
        Set<String> enabledPrograms = new HashSet<String>();
        // Find my current association
        for (Entity school : schools) {
            String schoolId = (String) school.getBody().get(ParameterConstants.SCHOOL_ID);
            teacherSchoolIds.add(schoolId);
            basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL,
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
        basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL,
                SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.PROGRAM_ID, NeutralCriteria.CRITERIA_IN,
                new ArrayList<String>(enabledPrograms)));
        Iterable<Entity> staffCas = repo.findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, basicQuery);
        // Look at only the SCAs for programs in my edorg with date/record access
        for (Entity sca : staffCas) {
            String programId = (String) sca.getBody().get(ParameterConstants.PROGRAM_ID);
            staffProgramIds.add(programId);
        }
        Collection<String> validated = new ArrayList<String>();
        // Get studentProgramAssociations
        for (String id : ids) {
            basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            Iterable<Entity> studentPrograms = repo.findAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, basicQuery);
            for (Entity studentProgram : studentPrograms) {
                // TODO End date Filtering
                studentProgramIds.add((String) studentProgram.getBody().get(ParameterConstants.PROGRAM_ID));
            }
            Set<String> tempSet = new HashSet<String>(staffProgramIds);
            tempSet.retainAll(studentProgramIds);
            if (tempSet.size() == 0 && quitOnFirstInvalid) {
                return validated;
            } else {
                validated.add(id);
            }

        }
        return validated;
    }


    private Collection<String> validatedWithCohorts(Set<String> ids, boolean quitOnFirstInvalid) {
        // Get my edorg association
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.TEACHER_ID,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        Iterable<Entity> schools = repo.findAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, basicQuery);
        Set<String> teacherSchoolIds = new HashSet<String>();
        Set<String> staffCohortIds = new HashSet<String>();
        Set<String> studentCohortIds = new HashSet<String>();
        // Find my current association
        if (schools != null) {
            for (Entity school : schools) {
                teacherSchoolIds.add((String) school.getBody().get(ParameterConstants.SCHOOL_ID));
            }
        }

        // Get my staffCohortAssociations
        basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL,
                SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        Iterable<Entity> staffCas = repo.findAll(EntityNames.STAFF_COHORT_ASSOCIATION, basicQuery);
        // Look at only the SCAs for cohorts in my edorg with date/record access
        if (staffCas != null) {
            for (Entity sca : staffCas) {
                String cohortId = (String) sca.getBody().get(ParameterConstants.COHORT_ID);
                basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                        NeutralCriteria.OPERATOR_EQUAL, cohortId));
                Iterable<Entity> cohorts = repo.findAll(EntityNames.COHORT, basicQuery);
                for (Entity cohort : cohorts) {
                    String edorgId = (String) cohort.getBody().get("educationOrgId");
                    if (teacherSchoolIds.contains(edorgId) && !staffCohortIds.contains(cohort.getEntityId())) {
                        // TODO End date filtering
                        staffCohortIds.add(cohort.getEntityId());
                    }
                }
            }
        }
        Collection<String> validated = new ArrayList<String>();
        // Get studentCohortAssociations
        for (String id : ids) {
            basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            Iterable<Entity> studentCohorts = repo.findAll(EntityNames.STUDENT_COHORT_ASSOCIATION, basicQuery);
            if (studentCohorts != null) {
                for (Entity studentCohort : studentCohorts) {
                    // TODO End date Filtering
                    studentCohortIds.add((String) studentCohort.getBody().get(ParameterConstants.COHORT_ID));
                }
            }

            Set<String> tempSet = new HashSet<String>(staffCohortIds);
            tempSet.retainAll(studentCohortIds);
            if (tempSet.size() == 0 && quitOnFirstInvalid) {
                return validated;
            } else {
                validated.add(id);
            }

        }
        return validated;
    }

    private Collection<String> validatedWithSections(Set<String> ids, boolean quitOnFirstInvalid) {
        Set<String> teacherSections = new HashSet<String>();

        NeutralCriteria endDateCriteria = new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_GTE, getFilterDate());

        NeutralQuery basicQuery = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.TEACHER_ID, NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                        .getSLIPrincipal().getEntity().getEntityId()));
        basicQuery.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_EXISTS, false)));
        basicQuery.addOrQuery(new NeutralQuery(endDateCriteria));

        Iterable<Entity> tsas = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, basicQuery);
        if (tsas != null) {
            for (Entity tsa : tsas) {
                teacherSections.add((String) tsa.getBody().get(ParameterConstants.SECTION_ID));
            }
        }
        Collection<String> validated = new ArrayList<String>();
        for (String id : ids) {
            Set<String> studentSections = new HashSet<String>();
            basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            // basicQuery.addCriteria(endDateCriteria);
            Iterable<Entity> ssas = repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, basicQuery);
            if (ssas != null) {
                for (Entity ssa : ssas) {
                    studentSections.add((String) ssa.getBody().get(ParameterConstants.SECTION_ID));
                }
            }

            Set<String> tempSet = new HashSet<String>(teacherSections);
            tempSet.retainAll(studentSections);
            if (tempSet.size() == 0 && quitOnFirstInvalid) {
                return validated;
            } else {
                validated.add(id);
            }

        }
        return validated;
    }


    @Override
    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
