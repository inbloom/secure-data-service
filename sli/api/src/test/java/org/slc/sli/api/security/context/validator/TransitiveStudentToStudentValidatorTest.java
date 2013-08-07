package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TransitiveStudentToStudentValidatorTest {
    
    @Autowired
    private TransitiveStudentToStudentValidator validator;
    
    @Resource
    private SecurityContextInjector inj;
    
    @Resource
    private ValidatorTestHelper helper;
    
    @Autowired
    private DateHelper dateHelper;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    private static final String AUTHENTICATED_STUDENT = "authenticated_student_id";
    
    Entity student;
    
    @Before
    public void setup() {
        student = Mockito.mock(Entity.class);
        Mockito.when(student.getType()).thenReturn("student");
        Mockito.when(student.getEntityId()).thenReturn(AUTHENTICATED_STUDENT);
        
        // section 1
        Entity section1 = helper.generateSection("edorg_1");
        section1.getEmbeddedData().put("studentSectionAssociation", new ArrayList<Entity>());
        Map<String, Object> ssa1Body = new HashMap<String, Object>();
        ssa1Body.put("studentId", "student_1");
        ssa1Body.put("endDate", DateTime.now().plus(Period.days(1)).toString(dateHelper.getDateTimeFormat()));
        Entity ssa1 = new MongoEntity("studentSectionAssociation", "ssa_1", ssa1Body, null);
        
        Map<String, Object> ssa2Body = new HashMap<String, Object>();
        ssa2Body.put("studentId", "student_2");
        ssa2Body.put("endDate", DateTime.now().plus(Period.days(1)).toString(dateHelper.getDateTimeFormat()));
        Entity ssa2 = new MongoEntity("studentSectionAssociation", "ssa_2", ssa2Body, null);
        
        Map<String, Object> ssa5Body = new HashMap<String, Object>();
        ssa5Body.put("studentId", "student_5");
        ssa5Body.put("endDate", DateTime.now().minus(Period.days(1)).toString(dateHelper.getDateTimeFormat()));
        Entity ssa5 = new MongoEntity("studentSectionAssociation", "ssa_5", ssa5Body, null);
        
        section1.getEmbeddedData().get("studentSectionAssociation").add(ssa1);
        section1.getEmbeddedData().get("studentSectionAssociation").add(ssa2);
        section1.getEmbeddedData().get("studentSectionAssociation").add(ssa5);
        
        // section 2
        Entity section2 = helper.generateSection("edorg_1");
        section2.getEmbeddedData().put("studentSectionAssociation", new ArrayList<Entity>());
        Map<String, Object> ssa3Body = new HashMap<String, Object>();
        ssa3Body.put("studentId", "student_3");
        ssa3Body.put("endDate", DateTime.now().plus(Period.days(1)).toString(dateHelper.getDateTimeFormat()));
        Entity ssa3 = new MongoEntity("studentSectionAssociation", "ssa_3", ssa3Body, null);
        
        Map<String, Object> ssa4Body = new HashMap<String, Object>();
        ssa4Body.put("studentId", "student_4");
        ssa4Body.put("endDate", DateTime.now().plus(Period.days(1)).toString(dateHelper.getDateTimeFormat()));
        Entity ssa4 = new MongoEntity("studentSectionAssociation", "ssa_4", ssa4Body, null);
        
        section2.getEmbeddedData().get("studentSectionAssociation").add(ssa3);
        section2.getEmbeddedData().get("studentSectionAssociation").add(ssa4);
        
        // denormalized data on authenticated student
        // 1 current section, 1 expired section, each with 2 students
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();
        Map<String, Object> denormSection1 = new HashMap<String, Object>();
        denormSection1.put("_id", section1.getEntityId());
        denormSection1.put("endDate", DateTime.now().plus(Period.days(1)).toString(dateHelper.getDateTimeFormat()));
        
        Map<String, Object> denormSection2 = new HashMap<String, Object>();
        denormSection2.put("_id", section2.getEntityId());
        denormSection2.put("endDate", DateTime.now().minus(Period.days(1)).toString(dateHelper.getDateTimeFormat()));
        
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(denormSection1);
        list.add(denormSection2);
        denormalized.put("section", list);
        Mockito.when(student.getDenormalizedData()).thenReturn(denormalized);
        
        inj.setStudentContext(student);
    }
    
    @Test
    public void testCurrentProgram() {
        Entity goodStudent = helper.generateStudent();
        goodStudent.getEmbeddedData().put("studentProgramAssociation", new ArrayList<Entity>());
        goodStudent.getEmbeddedData().get("studentProgramAssociation")
                .add(helper.generateStudentProgram(goodStudent.getEntityId(), "program_1", false));
        
        ArrayList<Entity> studentPrograms = new ArrayList<Entity>();
        studentPrograms.add(helper.generateStudentProgram(student.getEntityId(), "program_1", false));
        Map<String, List<Entity>> embeddedData = new HashMap<String,List<Entity>>();
        embeddedData.put("studentProgramAssociation", studentPrograms);
        Mockito.when(student.getEmbeddedData()).thenReturn(embeddedData);
        
        Set<String> ids = new HashSet<String>();
        ids.add(goodStudent.getEntityId());
        assertTrue(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testCurrentCohort() {
        Entity goodStudent = helper.generateStudent();
        goodStudent.getEmbeddedData().put("studentCohortAssociation", new ArrayList<Entity>());
        goodStudent.getEmbeddedData().get("studentCohortAssociation")
                .add(helper.generateStudentCohort(goodStudent.getEntityId(), "cohort_1", false));
        
        ArrayList<Entity> studentCohorts = new ArrayList<Entity>();
        studentCohorts.add(helper.generateStudentCohort(student.getEntityId(), "cohort_1", false));
        Map<String, List<Entity>> embeddedData = new HashMap<String,List<Entity>>();
        embeddedData.put("studentCohortAssociation", studentCohorts);
        Mockito.when(student.getEmbeddedData()).thenReturn(embeddedData);
        
        Set<String> ids = new HashSet<String>();
        ids.add(goodStudent.getEntityId());
        assertTrue(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testExpiredCohort() {
        Entity goodStudent = helper.generateStudent();
        goodStudent.getEmbeddedData().put("studentCohortAssociation", new ArrayList<Entity>());
        goodStudent.getEmbeddedData().get("studentCohortAssociation")
                .add(helper.generateStudentCohort(goodStudent.getEntityId(), "cohort_1", false));
        
        ArrayList<Entity> studentCohorts = new ArrayList<Entity>();
        studentCohorts.add(helper.generateStudentCohort(student.getEntityId(), "cohort_1", true)); // auth student's assoc is expired
        Map<String, List<Entity>> embeddedData = new HashMap<String,List<Entity>>();
        embeddedData.put("studentCohortAssociation", studentCohorts);
        Mockito.when(student.getEmbeddedData()).thenReturn(embeddedData);
        
        Set<String> ids = new HashSet<String>();
        ids.add(goodStudent.getEntityId());
        assertFalse(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testExpiredProgram() {
        Entity goodStudent = helper.generateStudent();
        goodStudent.getEmbeddedData().put("studentProgramAssociation", new ArrayList<Entity>());
        goodStudent.getEmbeddedData().get("studentProgramAssociation")
                .add(helper.generateStudentProgram(goodStudent.getEntityId(), "program_1", false));
        
        ArrayList<Entity> studentPrograms = new ArrayList<Entity>();
        studentPrograms.add(helper.generateStudentProgram(student.getEntityId(), "program_1", true)); // auth student's assoc is expired
        Map<String, List<Entity>> embeddedData = new HashMap<String,List<Entity>>();
        embeddedData.put("studentProgramAssociation", studentPrograms);
        Mockito.when(student.getEmbeddedData()).thenReturn(embeddedData);
        
        Set<String> ids = new HashSet<String>();
        ids.add(goodStudent.getEntityId());
        assertFalse(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testExpiredCohortOtherStudent() {
        Entity goodStudent = helper.generateStudent();
        goodStudent.getEmbeddedData().put("studentCohortAssociation", new ArrayList<Entity>());
        goodStudent.getEmbeddedData().get("studentCohortAssociation")
                .add(helper.generateStudentCohort(goodStudent.getEntityId(), "cohort_1", true)); // other student's assoc is expired
        
        ArrayList<Entity> studentCohorts = new ArrayList<Entity>();
        studentCohorts.add(helper.generateStudentCohort(student.getEntityId(), "cohort_1", false));
        Map<String, List<Entity>> embeddedData = new HashMap<String,List<Entity>>();
        embeddedData.put("studentCohortAssociation", studentCohorts);
        Mockito.when(student.getEmbeddedData()).thenReturn(embeddedData);
        
        Set<String> ids = new HashSet<String>();
        ids.add(goodStudent.getEntityId());
        assertFalse(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testExpiredProgramOtherStudent() {
        Entity goodStudent = helper.generateStudent();
        goodStudent.getEmbeddedData().put("studentProgramAssociation", new ArrayList<Entity>());
        goodStudent.getEmbeddedData().get("studentProgramAssociation")
                .add(helper.generateStudentProgram(goodStudent.getEntityId(), "program_1", true)); // other student's assoc is expired
        
        ArrayList<Entity> studentPrograms = new ArrayList<Entity>();
        studentPrograms.add(helper.generateStudentProgram(student.getEntityId(), "program_1", false));
        Map<String, List<Entity>> embeddedData = new HashMap<String,List<Entity>>();
        embeddedData.put("studentProgramAssociation", studentPrograms);
        Mockito.when(student.getEmbeddedData()).thenReturn(embeddedData);
        
        Set<String> ids = new HashSet<String>();
        ids.add(goodStudent.getEntityId());
        assertFalse(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testParamValidation() {
        assertTrue(validator.canValidate("student", true));
        assertFalse(validator.canValidate("student", false));
        assertTrue(validator.validate("student", new HashSet<String>()).isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWrongType() {
        HashSet<String> ids = new HashSet<String>();
        ids.add("test");
        validator.validate("school", ids);
    }
    
    @Test
    public void testSelf() {
        Set<String> ids = new HashSet<String>();
        ids.add(AUTHENTICATED_STUDENT);
        assertTrue(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testCurrentSections() {
        // current session
        Set<String> ids = new HashSet<String>();
        ids.add(AUTHENTICATED_STUDENT);
        ids.add("student_2");
        ids.add("student_1");
        assertTrue(validator.validate("student", ids).containsAll(ids));
        
    }
    
    @Test
    public void testExpiredSection() {
        Set<String> ids = new HashSet<String>();
        // student_3 is in expired section;
        ids.add("student_3");
        assertFalse(validator.validate("student", ids).containsAll(ids));
    }
    
    @Test
    public void testExpiredSectionAssoc() {
        // student_5 is in current session, but his/her association is expired
        Set<String> ids = new HashSet<String>();
        ids.add("student_5");
        assertFalse(validator.validate("student", ids).containsAll(ids));
    }
}
