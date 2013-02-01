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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Utility for unit testing context validators.
 */
@Component
public class ValidatorTestHelper {

    private static final String GRADING_PERIOD_REFERENCE = "gradingPeriodReference";

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;

    public static final String STAFF_ID = "1";
    public static final String ED_ORG_ID = "111";

    public String getBadDate() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime past = DateTime.now().minusYears(10);
        return past.toString(fmt);
    }

    public Entity generateStaffEdorg(String staffId, String edOrgId, boolean isExpired) {
        Map<String, Object> staffEdorg = new HashMap<String, Object>();
        staffEdorg.put(ParameterConstants.STAFF_REFERENCE, staffId);
        staffEdorg.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, edOrgId);
        expireAssociation(isExpired, staffEdorg);
        return repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, staffEdorg);
    }

    public Entity generateEdorgWithParent(String parentId) {
        Map<String, Object> edorg = new HashMap<String, Object>();
        if (parentId != null) {
            edorg.put(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE, parentId);
        }
        edorg.put("organizationCategories", Arrays.asList());
        return repo.create(EntityNames.EDUCATION_ORGANIZATION, edorg);
    }

    public Entity generateSection(String edorgId) {
        return generateSection(edorgId, null);
    }

    public Entity generateSectionWithCourseOffering(String edorgId, String courseOfferingId) {
        Map<String, Object> section = new HashMap<String, Object>();
        section.put(ParameterConstants.SCHOOL_ID, edorgId);
        section.put(ParameterConstants.COURSE_OFFERING_ID, courseOfferingId);
        return repo.create(EntityNames.SECTION, section);
    }

    public Entity generateSection(String edorgId, String sessionId) {
        Map<String, Object> section = new HashMap<String, Object>();
        section.put(ParameterConstants.SCHOOL_ID, edorgId);
        section.put(ParameterConstants.SESSION_ID, sessionId);
        return repo.create(EntityNames.SECTION, section);
    }

    public Entity generateSSA(String studentId, String sectionId, boolean isExpired) {
        Map<String, Object> ssaBody = new HashMap<String, Object>();
        ssaBody.put(ParameterConstants.SECTION_ID, sectionId);
        ssaBody.put(ParameterConstants.STUDENT_ID, studentId);
        expireAssociation(isExpired, ssaBody);
        return repo.create(EntityNames.STUDENT_SECTION_ASSOCIATION, ssaBody);
    }

    private void expireAssociation(boolean isExpired, Map<String, Object> body) {
        if (isExpired) {
            body.put(ParameterConstants.END_DATE, getBadDate());
        }
    }

    public Entity generateTeacherSchool(String teacherId, String edorgId) {
        generateStaffEdorg(teacherId, edorgId, false);
        Map<String, Object> tsaBody = new HashMap<String, Object>();
        tsaBody.put(ParameterConstants.TEACHER_ID, teacherId);
        tsaBody.put(ParameterConstants.SCHOOL_ID, edorgId);

        return repo.create(EntityNames.TEACHER_SCHOOL_ASSOCIATION, tsaBody);
    }

    public Entity generateCourse(String edorgId) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.SCHOOL_ID, edorgId);
        return repo.create(EntityNames.COURSE, body);
    }

    public Entity generateTSA(String teacherId, String sectionId, boolean isExpired) {
        Map<String, Object> tsaBody = new HashMap<String, Object>();
        tsaBody.put(ParameterConstants.TEACHER_ID, teacherId);
        tsaBody.put(ParameterConstants.SECTION_ID, sectionId);
        expireAssociation(isExpired, tsaBody);
        return repo.create(EntityNames.TEACHER_SECTION_ASSOCIATION, tsaBody);
    }

    public Entity generateCohort(String edOrgId) {
        Map<String, Object> cohortBody = new HashMap<String, Object>();
        cohortBody.put("educationOrgId", edOrgId);

        return repo.create(EntityNames.COHORT, cohortBody);
    }

    public Entity generateStaffCohort(String teacherId, String cohortId, boolean isExpired, boolean studentAccess) {
        Map<String, Object> staffCohort = new HashMap<String, Object>();
        staffCohort.put(ParameterConstants.STAFF_ID, teacherId);
        staffCohort.put(ParameterConstants.COHORT_ID, cohortId);
        expireAssociation(isExpired, staffCohort);
        staffCohort.put(ParameterConstants.STUDENT_RECORD_ACCESS, studentAccess);

        return repo.create(EntityNames.STAFF_COHORT_ASSOCIATION, staffCohort);

    }

    public String generateStudentAndStudentSchoolAssociation(String studentId, String schoolId, boolean isExpired) {
        return generateStudentAndStudentSchoolAssociation(studentId, schoolId, null, isExpired);
    }
    
    public Entity generateStudent() {
        Map<String, Object> student = new HashMap<String, Object>();
        student.put("studentUniqueStateId", "BLAH");
        student.put("sex", "Female");
        Map<String, Object> birthDate = new HashMap<String, Object>();
        birthDate.put("birthDate", "2003-04-03");
        student.put("birthData", birthDate);
        student.put("hispanicLatinoEthnicity", false);
        Entity entity = repo.create(EntityNames.STUDENT, student);
        return entity;
    }

    public String generateStudentAndStudentSchoolAssociation(String studentId, String schoolId,
            String graduationPlanId, boolean isExpired) {
        Map<String, Object> student = new HashMap<String, Object>();
        student.put("studentUniqueStateId", studentId);
        student.put("sex", "Female");
        Map<String, Object> birthDate = new HashMap<String, Object>();
        birthDate.put("birthDate", "2003-04-03");
        student.put("birthData", birthDate);
        student.put("hispanicLatinoEthnicity", false);

        Map<String, List<Map<String, Object>>> denormalizations = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> schools = new ArrayList<Map<String, Object>>();
        Map<String, Object> school = new HashMap<String, Object>();
        school.put("_id", schoolId);
        school.put("entryDate", DateTime.now().minusDays(3).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        school.put("entryGradeLevel", "Fifth grade");
        if (isExpired) {
            school.put("exitWithdrawDate", getBadDate());
        }
        school.put("edOrgs", Arrays.asList(schoolId));
        schools.add(school);
        denormalizations.put("schools", schools);
        student.put("denormalization", denormalizations);

        Entity entity = repo.create(EntityNames.STUDENT, student);
        String createdStudentId = entity.getEntityId();

        generateStudentSchoolAssociation(createdStudentId, schoolId, graduationPlanId, isExpired);

        return createdStudentId;
    }

    public Entity generateStudentSchoolAssociation(String studentId, String schoolId, String graduationPlanId,
            boolean isExpired) {
        Map<String, Object> association = new HashMap<String, Object>();
        association.put(ParameterConstants.STUDENT_ID, studentId);
        association.put(ParameterConstants.SCHOOL_ID, schoolId);
        association.put("entryDate", DateTime.now().minusDays(3).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        association.put("entryGradeLevel", "Fifth grade");
        association.put("graduationPlanId", graduationPlanId);
        expireStudentSchoolAssociation(isExpired, association);
        Entity created = repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, association);
        return created;
    }
    
    public Entity generateAttendance(String studentId, String schoolId) {
        Map<String, Object> att = new HashMap<String, Object>();
        att.put(ParameterConstants.STUDENT_ID, studentId);
        att.put(ParameterConstants.SCHOOL_ID, schoolId);
        Entity created = repo.create(EntityNames.ATTENDANCE, att);
        return created;
    }

    private void expireStudentSchoolAssociation(boolean isExpired, Map<String, Object> body) {
        if (isExpired) {
            body.put("exitWithdrawDate", DateTime.now().minusDays(2).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        }
    }

    public Entity generateStudentCohort(String studentId, String cohortId, boolean isExpired) {
        Map<String, Object> studentCohort = new HashMap<String, Object>();
        studentCohort.put(ParameterConstants.STUDENT_ID, studentId);
        studentCohort.put(ParameterConstants.COHORT_ID, cohortId);
        expireAssociation(isExpired, studentCohort);

        return repo.create(EntityNames.STUDENT_COHORT_ASSOCIATION, studentCohort);
    }

    public Entity generateStudentProgram(String studentId, String programId, String edorgId, boolean isExpired) {
        Map<String, Object> studentProgram = new HashMap<String, Object>();
        studentProgram.put(ParameterConstants.STUDENT_ID, studentId);
        studentProgram.put(ParameterConstants.PROGRAM_ID, programId);
        if (edorgId != null) {
            studentProgram.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, edorgId);
        }
        expireAssociation(isExpired, studentProgram);

        return repo.create(EntityNames.STUDENT_PROGRAM_ASSOCIATION, studentProgram);
    }

    public Entity generateStudentProgram(String studentId, String programId, boolean isExpired) {
        return generateStudentProgram(studentId, programId, null, isExpired);
    }

    public Entity generateEdorgWithProgram(List<String> programIds) {
        Map<String, Object> edorgBody = new HashMap<String, Object>();
        edorgBody.put(ParameterConstants.PROGRAM_REFERENCE, programIds);
        return repo.create(EntityNames.EDUCATION_ORGANIZATION, edorgBody);
    }

    public Entity generateProgram() {
        return repo.create(EntityNames.PROGRAM, new HashMap<String, Object>());
    }

    public Entity generateTeacher() {
        return repo.create(EntityNames.TEACHER, new HashMap<String, Object>());
    }

    public Entity generateStaff() {
        return repo.create(EntityNames.STAFF, new HashMap<String, Object>());
    }

    public Entity generateStaffProgram(String teacherId, String programId, boolean isExpired, boolean studentAccess) {
        Map<String, Object> staffProgram = new HashMap<String, Object>();
        staffProgram.put(ParameterConstants.STAFF_ID, teacherId);
        staffProgram.put(ParameterConstants.PROGRAM_ID, programId);
        expireAssociation(isExpired, staffProgram);
        staffProgram.put(ParameterConstants.STUDENT_RECORD_ACCESS, studentAccess);

        return repo.create(EntityNames.STAFF_PROGRAM_ASSOCIATION, staffProgram);

    }

    public Entity generateAssessment() {
        return repo.create(EntityNames.ASSESSMENT, new HashMap<String, Object>());
    }

    public Entity generateLearningObjective() {
        return repo.create(EntityNames.LEARNING_OBJECTIVE, new HashMap<String, Object>());
    }

    public Entity generateLearningStandard() {
        return repo.create(EntityNames.LEARNING_STANDARD, new HashMap<String, Object>());
    }

    public Entity generateDisciplineIncident(String schoolId) {
        Map<String, Object> diBody = new HashMap<String, Object>();
        diBody.put(ParameterConstants.SCHOOL_ID, schoolId);
        return repo.create(EntityNames.DISCIPLINE_INCIDENT, diBody);
    }

    public Entity generateDisciplineIncident(String schoolId, String... staffIds) {
        Map<String, Object> diBody = new HashMap<String, Object>();
        diBody.put(ParameterConstants.SCHOOL_ID, schoolId);
        HashSet<String> staffList = new HashSet<String>();
        for (String staffId : staffIds) {
            staffList.add(staffId);
        }
        diBody.put(ParameterConstants.STAFF_ID, staffList);
        return repo.create(EntityNames.DISCIPLINE_INCIDENT, diBody);
    }

    public Entity generateStudentDisciplineIncidentAssociation(String studentId, String disciplineId) {
        Map<String, Object> sdia = new HashMap<String, Object>();
        sdia.put(ParameterConstants.STUDENT_ID, studentId);
        sdia.put(ParameterConstants.DISCIPLINE_INCIDENT_ID, disciplineId);
        return repo.create(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, sdia);
    }

    public Entity generateSession(String schoolId, List<String> gradingPeriodRefs) {
        Map<String, Object> session = new HashMap<String, Object>();
        session.put(ParameterConstants.SCHOOL_ID, schoolId);
        if (null != gradingPeriodRefs) {
            session.put(GRADING_PERIOD_REFERENCE, gradingPeriodRefs);
        }
        return repo.create(EntityNames.SESSION, session);
    }

    public Entity generateCourseOffering(String schoolId) {
        Map<String, Object> courseOffering = new HashMap<String, Object>();
        courseOffering.put(ParameterConstants.SCHOOL_ID, schoolId);
        return repo.create(EntityNames.COURSE_OFFERING, courseOffering);
    }

    public Entity generateCourseOffering(String schoolId, String courseId) {
        Map<String, Object> courseOffering = new HashMap<String, Object>();
        courseOffering.put(ParameterConstants.SCHOOL_ID, schoolId);
        courseOffering.put(ParameterConstants.COURSE_ID, courseId);
        return repo.create(EntityNames.COURSE_OFFERING, courseOffering);
    }

    public Entity generateGradingPeriod() {
        return repo.create(EntityNames.GRADING_PERIOD, new HashMap<String, Object>());
    }

    public Entity generateStudentCompetencyObjective(String edorgId) {
        Map<String, Object> scObj = new HashMap<String, Object>();
        scObj.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, edorgId);
        return repo.create(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObj);
    }

    public Entity generateGraduationPlan(String edorgId) {
        Map<String, Object> gradPlan = new HashMap<String, Object>();
        gradPlan.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, edorgId);
        return repo.create(EntityNames.GRADUATION_PLAN, gradPlan);
    }

    public Entity generateGrade(String studentSectionAssociationId) {
        Map<String, Object> grade = new HashMap<String, Object>();
        grade.put("letterGradeEarned", "A");
        grade.put("gradeType", "Exam");
        grade.put("studentSectionAssociationId", studentSectionAssociationId);
        return repo.create(EntityNames.GRADE, grade);
    }

    public Entity generateStudentCompetency(String studentSectionAssociationId, String objectiveId) {
        Map<String, Object> grade = new HashMap<String, Object>();
        grade.put("diagnosticStatement", "blah");
        grade.put("studentSectionAssociationId", studentSectionAssociationId);
        grade.put("objectiveId", objectiveId);
        return repo.create(EntityNames.STUDENT_COMPETENCY, grade);
    }
    
    public Entity generateParent() {
        Map<String, Object> parent = new HashMap<String, Object>();
        parent.put(ParameterConstants.PARENT_UNIQUE_STATE_ID, "PARENT_ID");
        parent.put("name", "Mama Cass");
        return repo.create(EntityNames.PARENT, parent);
    }
    
    public Entity generateStudentParentAssoc(String studentId, String parentId) {
        Map<String, Object> parent = new HashMap<String, Object>();
        parent.put(ParameterConstants.PARENT_ID, parentId);
        parent.put(ParameterConstants.STUDENT_ID, studentId);
        return repo.create(EntityNames.STUDENT_PARENT_ASSOCIATION, parent);
    }

    protected void setUpTeacherContext() {
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("teacher");
        Mockito.when(entity.getEntityId()).thenReturn(STAFF_ID);
        injector.setCustomContext(user, fullName, "DERPREALM", roles, entity, ED_ORG_ID);
    }

    public void resetRepo() throws Exception {
        Field[] fields = EntityNames.class.getDeclaredFields();

        for (Field f : fields) {
            if (f.getType() == String.class && Modifier.isStatic(f.getModifiers())) {
                String ent = (String) f.get(null);
                repo.deleteAll(ent, new NeutralQuery());
            }
        }

    }

}
