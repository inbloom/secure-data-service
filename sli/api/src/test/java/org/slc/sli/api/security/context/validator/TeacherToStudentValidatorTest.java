package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TeacherToStudentValidatorTest {
    
    @Autowired
    private TeacherToStudentValidator validator;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    private PagingRepositoryDelegate<Entity> mockRepo;
    
    private Set<String> studentIds;
    
    private String badDate;

    @Before
    public void setUp() {
        // Set up the principal
        String user = "fake teacher";
        String fullName = "Fake Teacher";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);
        
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("teacher");
        Mockito.when(entity.getEntityId()).thenReturn("1");
        injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, "111");
        mockRepo = mock(PagingRepositoryDelegate.class);
        validator.setRepo(mockRepo);
        
        studentIds = new HashSet<String>();
        
        badDate = Integer.parseInt(gracePeriod) * -1 - 5 + "";

    }
    
    @After
    public void tearDown() {
        mockRepo = null;
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidateTeacherToStudent() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT));
    }
    
    @Test
    public void testCanNotValidateOtherEntities() throws Exception {
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE));
    }
    
    @Test
    public void testCanGetAccessThroughSingleValidStudent() throws Exception {
        Map<String, Object> tsaBody = generateTSA("1", "3", false);
        Entity tsa = new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, tsaBody);
        Map<String, Object> ssaBody = generateSSA("2", "3", false);
        Entity ssa = new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, ssaBody);

        
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(ssa));
        
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(tsa));
        studentIds.add("3");
        assertTrue(validator.validate(studentIds));
    }
    
    @Test
    public void testCanNotGetAccessThroughInvalidStudent() throws Exception {
        Map<String, Object> tsaBody = generateTSA("1", "-1", false);
        Entity tsa = new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, tsaBody);
        Map<String, Object> ssaBody = generateSSA("2", "3", false);
        Entity ssa = new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, ssaBody);
        
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(ssa));
        
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(tsa));
        studentIds.add("3");
        assertFalse(validator.validate(studentIds));
    }
    
    @Test
    public void testCanGetAccessThroughManyStudents() throws Exception {
        List<Entity> tsas = new ArrayList<Entity>();
        for (int i = 0; i < 100; ++i) {
            tsas.add(new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, generateTSA("1", "" + i, false)));
        }
        List<Entity> ssas = new ArrayList<Entity>();
        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                ssas.add(new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, generateSSA("" + j, "" + i, false)));
                studentIds.add("" + j);
            }
        }
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(ssas);
        
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(tsas);
        assertTrue(validator.validate(studentIds));
    }
    
    @Test
    public void testCanNotGetAccessThroughManyStudents() throws Exception {
        List<Entity> tsas = new ArrayList<Entity>();
        for (int i = 100; i < 200; ++i) {
            tsas.add(new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, generateTSA("1", "" + i, false)));
        }
        List<Entity> ssas = new ArrayList<Entity>();
        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                ssas.add(new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, generateSSA("" + j, "" + i, false)));
                studentIds.add("" + j);
            }
        }
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(ssas);
        
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(tsas);
        assertFalse(validator.validate(studentIds));
    }
    
    @Test
    public void testCanNotGetAccessThroughManyStudentsWithOneFailure() throws Exception {
        List<Entity> tsas = new ArrayList<Entity>();
        for (int i = 0; i < 100; ++i) {
            tsas.add(new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, generateTSA("1", "" + i, false)));
        }
        List<Entity> ssas = new ArrayList<Entity>();
        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                ssas.add(new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, generateSSA("" + j, "" + i, false)));
                studentIds.add("" + j);
            }
        }
        ssas.add(new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, generateSSA("" + -32, "" + 101, false)));
        studentIds.add("" + -32);
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(ssas);
        
        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(tsas);
        assertFalse(validator.validate(studentIds));
    }

    private Map<String, Object> generateSSA(String studentId, String sectionId, boolean isExpired) {
        Map<String, Object> ssaBody = new HashMap<String, Object>();
        ssaBody.put(ParameterConstants.SECTION_ID, sectionId);
        ssaBody.put(ParameterConstants.STUDENT_ID, studentId);
        if (isExpired) {
            ssaBody.put(ParameterConstants.END_DATE, validator.getFilterDate(badDate));
        }
        return ssaBody;
    }
    
    private Map<String, Object> generateTSA(String teacherId, String sectionId, boolean isExpired) {
        Map<String, Object> tsaBody = new HashMap<String, Object>();
        tsaBody.put("teacherId", teacherId);
        tsaBody.put("sectionId", sectionId);
        if (isExpired) {
            tsaBody.put(ParameterConstants.END_DATE, validator.getFilterDate(badDate));
        }

        return tsaBody;
    }

}
