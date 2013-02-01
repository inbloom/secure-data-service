/*
 * Copyright 2013 Shared Learning Collaborative, LLC
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

import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.validator.ValidatorTestHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EntityOwnershipValidatorTest {
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    EntityOwnershipValidator validator;
    
    Entity myEdOrg = null;
    Entity otherEdorg = null;
    
    @Before
    public void setup() throws Exception {
        myEdOrg = helper.generateEdorgWithParent(null);
        otherEdorg = helper.generateEdorgWithParent(null);
        injector.setEducatorContext();
        SecurityUtil.getSLIPrincipal().setAuthorizingEdOrgs(new HashSet<String>(Arrays.asList(myEdOrg.getEntityId())));
    }
    
    @After
    public void tearDown() throws Exception {
        helper.resetRepo();
    }
    
    @Test
    public void testAssessment() {
        //public
        Entity assessment = helper.generateAssessment();
        Assert.assertTrue(validator.canAccess(assessment));
    }
    
    @Test
    public void testEdorg() {
        //public
        Entity edorg = helper.generateEdorgWithParent(null);
        Assert.assertTrue(validator.canAccess(edorg));
    }
    
    @Test
    public void testLearningObj() {
        //public
        Entity obj = helper.generateLearningObjective();
        Assert.assertTrue(validator.canAccess(obj));
    }
    
    @Test
    public void testLearningStd() {
        //public
        Entity ls = helper.generateLearningStandard();
        Assert.assertTrue(validator.canAccess(ls));
    }
    
    @Test
    public void testProgram() {
        //public
        Entity prog = helper.generateProgram();
        Assert.assertTrue(validator.canAccess(prog));
    }
    
    @Test
    public void testGradingPeriod() {
        //public
        Entity gp = helper.generateGradingPeriod();
        Assert.assertTrue(validator.canAccess(gp));
    }
    
    @Test
    public void testStudent() {
        Entity student = helper.generateStudent();
        helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Assert.assertFalse(validator.canAccess(student));
        helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        Assert.assertTrue(validator.canAccess(student));
    }
    
    @Test
    public void testStudentSchoolAssociation() {
        Entity student = helper.generateStudent();
        Entity ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Assert.assertFalse(validator.canAccess(ssa));
        ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        Assert.assertTrue(validator.canAccess(student));
    }
    
    @Test
    public void testStudentSectionAssoc() {
        Entity student = helper.generateStudent();
        helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Entity section = helper.generateSection(otherEdorg.getEntityId());
        Entity ssa = helper.generateSSA(student.getEntityId(), section.getEntityId(), false);
        Assert.assertFalse(validator.canAccess(ssa));
        
        section = helper.generateSection(myEdOrg.getEntityId());
        helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        ssa = helper.generateSSA(student.getEntityId(), section.getEntityId(), false);
        Assert.assertTrue(validator.canAccess(ssa));
    }
    
    @Test
    public void testGrade() {
        Entity student = helper.generateStudent();
        helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Entity section = helper.generateSection(otherEdorg.getEntityId());
        Entity ssa = helper.generateSSA(student.getEntityId(), section.getEntityId(), false);
        Entity grade = helper.generateGrade(ssa.getEntityId());
        Assert.assertFalse(validator.canAccess(grade));
        
        section = helper.generateSection(myEdOrg.getEntityId());
        helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        ssa = helper.generateSSA(student.getEntityId(), section.getEntityId(), false);
        grade = helper.generateGrade(ssa.getEntityId());
        Assert.assertTrue(validator.canAccess(grade));
    }
    
    @Test
    public void testAttendance() {
        Entity att = helper.generateAttendance("blah", otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(att));
        att = helper.generateAttendance("blah", myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(att));
    }
    
    @Test
    public void testCohort() {
        Entity cohort = helper.generateCohort(otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(cohort));
        cohort = helper.generateCohort(myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(cohort));
    }
    
    @Test
    public void testCourse() {
        Entity course = helper.generateCourse(otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(course));
        course = helper.generateCourse(myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(course));
    }
    
    @Test
    public void testCourseOffering() {
        Entity offering = helper.generateCourseOffering(otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(offering));
        offering = helper.generateCourseOffering(myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(offering));
    }
    
    @Test
    public void testDisciplineIncident() {
        Entity di = helper.generateDisciplineIncident(otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(di));
        di = helper.generateDisciplineIncident(myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(di));
    }
    
    @Test
    public void testGraduationPlan() {
        Entity gp = helper.generateGraduationPlan(otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(gp));
        gp = helper.generateGraduationPlan(myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(gp));
    }
    
    @Test
    public void testParent() {
        Entity student = helper.generateStudent();
        Entity ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Entity parent = helper.generateParent();
        helper.generateStudentParentAssoc(student.getEntityId(), parent.getEntityId());
        Assert.assertFalse(validator.canAccess(parent));
        ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        Assert.assertTrue(validator.canAccess(student));
    }
    
    @Test
    public void testStudentParentAssoc() {
        Entity student = helper.generateStudent();
        Entity ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Entity parent = helper.generateParent();
        Entity spa = helper.generateStudentParentAssoc(student.getEntityId(), parent.getEntityId());
        Assert.assertFalse(validator.canAccess(spa));
        ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        Assert.assertTrue(validator.canAccess(spa));
    }
    
    @Test
    public void testSection() {
        Entity sec = helper.generateSection(otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(sec));
        sec = helper.generateSection(myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(sec));
    }
    
    @Test
    public void testSession() {
        Entity session = helper.generateSession(otherEdorg.getEntityId(), null);
        Assert.assertFalse(validator.canAccess(session));
        session = helper.generateSession(myEdOrg.getEntityId(), null);
        Assert.assertTrue(validator.canAccess(session));
    }
    
    @Test
    public void testStaff() {
        Entity staff = helper.generateStaff();
        helper.generateStaffEdorg(staff.getEntityId(), otherEdorg.getEntityId(), false);
        Assert.assertFalse(validator.canAccess(staff));
        helper.generateStaffEdorg(staff.getEntityId(), myEdOrg.getEntityId(), false);
        Assert.assertTrue(validator.canAccess(staff));
    }
    
    @Test
    public void testStaffEdOrg() {
        Entity staff = helper.generateStaff();
        Entity staffEdorg = helper.generateStaffEdorg(staff.getEntityId(), otherEdorg.getEntityId(), false);
        Assert.assertFalse(validator.canAccess(staffEdorg));
        staffEdorg = helper.generateStaffEdorg(staff.getEntityId(), myEdOrg.getEntityId(), false);
        Assert.assertTrue(validator.canAccess(staffEdorg));
    }
    
    @Test
    public void testStaffCohort() {
        Entity staff = helper.generateStaff();
        helper.generateStaffEdorg(staff.getEntityId(), otherEdorg.getEntityId(), false);
        Entity staffCohort = helper.generateStaffCohort(staff.getEntityId(), "cohortId", false, true);
        Assert.assertFalse(validator.canAccess(staffCohort));
        helper.generateStaffEdorg(staff.getEntityId(), myEdOrg.getEntityId(), false);
        Assert.assertTrue(validator.canAccess(staffCohort));
    }
    
    @Test
    public void testStaffProgram() {
        Entity staff = helper.generateStaff();
        helper.generateStaffEdorg(staff.getEntityId(), otherEdorg.getEntityId(), false);
        Entity staffProgram = helper.generateStaffProgram(staff.getEntityId(), "programId", false, true);
        Assert.assertFalse(validator.canAccess(staffProgram));
        helper.generateStaffEdorg(staff.getEntityId(), myEdOrg.getEntityId(), false);
        Assert.assertTrue(validator.canAccess(staffProgram));
    }
    
    @Test
    public void testStudentCohortAssoc() {
        Entity student = helper.generateStudent();
        Entity ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Entity cohort = helper.generateCohort(otherEdorg.getEntityId());
        Entity studentCohort = helper.generateStudentCohort(student.getEntityId(), cohort.getEntityId(), false);
        Assert.assertFalse(validator.canAccess(studentCohort));
        ssa = helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        Assert.assertTrue(validator.canAccess(studentCohort));
    }
    
    @Test
    public void testStudentCompetency() {
        Entity student = helper.generateStudent();
        helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Entity section = helper.generateSection(otherEdorg.getEntityId());
        Entity ssa = helper.generateSSA(student.getEntityId(), section.getEntityId(), false);
        Entity comp = helper.generateStudentCompetency(ssa.getEntityId(), "objid");
        Assert.assertFalse(validator.canAccess(comp));
        
        section = helper.generateSection(myEdOrg.getEntityId());
        helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        ssa = helper.generateSSA(student.getEntityId(), section.getEntityId(), false);
        comp = helper.generateStudentCompetency(ssa.getEntityId(), "objid");
        Assert.assertTrue(validator.canAccess(ssa));
    }
    
    @Test
    public void testStudentCompObj() {
        Entity obj = helper.generateStudentCompetencyObjective(otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(obj));
        obj = helper.generateStudentCompetencyObjective(myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(obj));
    }
    
    @Test
    public void testStudentDiscIncAssoc() {
        Entity student = helper.generateStudent();
        helper.generateStudentSchoolAssociation(student.getEntityId(), otherEdorg.getEntityId(), "", false);
        Entity discIncAssoc = helper.generateStudentDisciplineIncidentAssociation(student.getEntityId(), "dicpId");
        Assert.assertFalse(validator.canAccess(discIncAssoc));
        helper.generateStudentSchoolAssociation(student.getEntityId(), myEdOrg.getEntityId(), "", false);
        Assert.assertTrue(validator.canAccess(discIncAssoc));
    }
    
    @Test
    public void testStudentProgramAssoc() {
        Entity spa = helper.generateStudentProgram("studentId", "programId", otherEdorg.getEntityId(), false);
        Assert.assertFalse(validator.canAccess(spa));
        spa = helper.generateStudentProgram("studentId", "programId", myEdOrg.getEntityId(), false);
        Assert.assertTrue(validator.canAccess(spa));
    }
    
    @Test
    public void testTeacherSectionAssoc() {
        Entity staff = helper.generateStaff();
        helper.generateStaffEdorg(staff.getEntityId(), otherEdorg.getEntityId(), false);
        Entity staffSection = helper.generateTSA(staff.getEntityId(), "sectionId", false);
        Assert.assertFalse(validator.canAccess(staffSection));
        helper.generateStaffEdorg(staff.getEntityId(), myEdOrg.getEntityId(), false);
        Assert.assertTrue(validator.canAccess(staffSection));
    }
    
    @Test
    public void testTeacherSchoolAssoc() {
        Entity tsa = helper.generateTeacherSchool("teacherId", otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(tsa));
        tsa = helper.generateTeacherSchool("teacherId", myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(tsa));
    }
    
    @Test
    public void testTeacher() {
        Entity teacher = helper.generateTeacher();
        helper.generateTeacherSchool(teacher.getEntityId(), otherEdorg.getEntityId());
        Assert.assertFalse(validator.canAccess(teacher));
        helper.generateTeacherSchool(teacher.getEntityId(), myEdOrg.getEntityId());
        Assert.assertTrue(validator.canAccess(teacher));
    }
    
    /*
        typeToReference.put(EntityNames.COURSE_TRANSCRIPT, new Reference(EntityNames.COURSE_TRANSCRIPT, EntityNames.COURSE, ParameterConstants.COURSE_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.DISCIPLINE_ACTION, new Reference(EntityNames.DISCIPLINE_ACTION, EntityNames.SCHOOL, "responsibilitySchoolId", Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADEBOOK_ENTRY, new Reference(EntityNames.GRADEBOOK_ENTRY, EntityNames.SECTION, ParameterConstants.SECTION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.REPORT_CARD, new Reference(EntityNames.REPORT_CARD, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ACADEMIC_RECORD, new Reference(EntityNames.STUDENT_ACADEMIC_RECORD, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ASSESSMENT, new Reference(EntityNames.STUDENT_ASSESSMENT, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_GRADEBOOK_ENTRY, new Reference(EntityNames.STUDENT_GRADEBOOK_ENTRY, EntityNames.GRADEBOOK_ENTRY, ParameterConstants.GRADEBOOK_ENTRY_ID, Reference.RefType.LEFT_TO_RIGHT));
     */


}
