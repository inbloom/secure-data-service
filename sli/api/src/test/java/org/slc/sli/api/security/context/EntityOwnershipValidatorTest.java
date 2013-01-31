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
    
    
    /*
     *         typeToReference.put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, new Reference(EntityNames.STUDENT_SCHOOL_ASSOCIATION, EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADE, new Reference(EntityNames.GRADE, EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_SECTION_ASSOCIATION, new Reference(EntityNames.STUDENT_SECTION_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.ATTENDANCE, new Reference(EntityNames.ATTENDANCE, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COHORT, new Reference(EntityNames.COHORT, EntityNames.EDUCATION_ORGANIZATION, "educationOrgId", Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COURSE, new Reference(EntityNames.COURSE, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COURSE_OFFERING, new Reference(EntityNames.COURSE_OFFERING, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COURSE_TRANSCRIPT, new Reference(EntityNames.COURSE_TRANSCRIPT, EntityNames.COURSE, ParameterConstants.COURSE_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.DISCIPLINE_INCIDENT, new Reference(EntityNames.DISCIPLINE_INCIDENT, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.DISCIPLINE_ACTION, new Reference(EntityNames.DISCIPLINE_ACTION, EntityNames.SCHOOL, "responsibilitySchoolId", Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADEBOOK_ENTRY, new Reference(EntityNames.GRADEBOOK_ENTRY, EntityNames.SECTION, ParameterConstants.SECTION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADUATION_PLAN, new Reference(EntityNames.GRADUATION_PLAN, EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.PARENT, new Reference(EntityNames.PARENT, EntityNames.STUDENT_PARENT_ASSOCIATION, ParameterConstants.PARENT_ID, Reference.RefType.RIGHT_TO_LEFT));
        typeToReference.put(EntityNames.REPORT_CARD, new Reference(EntityNames.REPORT_CARD, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.SECTION, new Reference(EntityNames.SECTION, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.SESSION, new Reference(EntityNames.SESSION, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF, new Reference(EntityNames.STAFF, EntityNames.STAFF_ED_ORG_ASSOCIATION, ParameterConstants.STAFF_REFERENCE, Reference.RefType.RIGHT_TO_LEFT));
        typeToReference.put(EntityNames.STAFF_ED_ORG_ASSOCIATION, new Reference(EntityNames.STAFF_ED_ORG_ASSOCIATION, EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF_COHORT_ASSOCIATION, new Reference(EntityNames.STAFF_COHORT_ASSOCIATION, EntityNames.STAFF, ParameterConstants.STAFF_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF_PROGRAM_ASSOCIATION, new Reference(EntityNames.STAFF_PROGRAM_ASSOCIATION, EntityNames.STAFF, ParameterConstants.STAFF_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ACADEMIC_RECORD, new Reference(EntityNames.STUDENT_ACADEMIC_RECORD, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ASSESSMENT, new Reference(EntityNames.STUDENT_ASSESSMENT, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COHORT_ASSOCIATION, new Reference(EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COMPETENCY, new Reference(EntityNames.STUDENT_COMPETENCY, EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new Reference(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new Reference(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_PARENT_ASSOCIATION, new Reference(EntityNames.STUDENT_PARENT_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_GRADEBOOK_ENTRY, new Reference(EntityNames.STUDENT_GRADEBOOK_ENTRY, EntityNames.GRADEBOOK_ENTRY, ParameterConstants.GRADEBOOK_ENTRY_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new Reference(EntityNames.STUDENT_PROGRAM_ASSOCIATION, EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER_SECTION_ASSOCIATION, new Reference(EntityNames.TEACHER_SECTION_ASSOCIATION, EntityNames.STAFF, ParameterConstants.TEACHER_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new Reference(EntityNames.TEACHER_SCHOOL_ASSOCIATION, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER, new Reference(EntityNames.TEACHER, EntityNames.STAFF_ED_ORG_ASSOCIATION, ParameterConstants.STAFF_REFERENCE, Reference.RefType.RIGHT_TO_LEFT));
        //typeToReference.put(EntityNames.STUDENT_GRADEBOOK_ENTRY, new Reference(EntityNames.STUDENT_GRADEBOOK_ENTRY, EntityNames.GRADEBOOK_ENTRY, ParameterConstants.GRADEBOOK_ENTRY_ID, Reference.RefType.LEFT_TO_RIGHT));
        publicEntities = new HashSet<String>(Arrays.asList(
     */


}
